package com.pdc.gatewayservice.configs;

import com.pdc.gatewayservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway filter for JWT authentication
 * Validates JWT tokens and adds user information to request headers
 * Excludes configured public paths from authentication
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String ROLE_HEADER = "X-User-Role";

    private final JwtUtil jwtUtil;
    private final PublicPathsConfig publicPathsConfig;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, PublicPathsConfig publicPathsConfig) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.publicPathsConfig = publicPathsConfig;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Skip JWT verification for public paths
            if (isPublicPath(request)) {
                log.debug("Accessing public path: {}", request.getURI().getPath());
                return chain.filter(exchange);
            }

            // Check for Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Missing authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Invalid authorization header");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            if (!jwtUtil.validateToken(token)) {
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
            }

            // Extract claims and modify request
            Claims claims = jwtUtil.extractClaims(token);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(USER_ID_HEADER, jwtUtil.extractUserId(claims))
                    .header(ROLE_HEADER, jwtUtil.extractRole(claims))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private boolean isPublicPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return publicPathsConfig
                .getPublicPaths()
                .stream()
                .anyMatch(publicPath -> {
                    // Handle both exact matches and wildcard patterns
                    if (publicPath.endsWith("/**")) {
                        String prefix = publicPath.substring(0, publicPath.length() - 3);
                        return path.startsWith(prefix);
                    }
                    return path.equals(publicPath);
                });
    }

    private Mono<Void> handleError(ServerWebExchange exchange, HttpStatus status, String message) {
        log.error("Authentication error: {}", message);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Data
    public static class Config {
        // Configuration properties if needed
    }
}