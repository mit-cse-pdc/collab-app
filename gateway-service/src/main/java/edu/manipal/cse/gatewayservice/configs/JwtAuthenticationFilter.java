package edu.manipal.cse.gatewayservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.manipal.cse.gatewayservice.exceptions.JwtTokenExpiredException;
import edu.manipal.cse.gatewayservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
            String path = request.getURI().getPath();

            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid authorization header");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());
            try {
                if (!jwtUtil.validateToken(token)) {
                    return handleError(exchange, HttpStatus.UNAUTHORIZED, "Token is expired or invalid");
                }

                Claims claims = jwtUtil.extractClaims(token);
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(USER_ID_HEADER, jwtUtil.extractUserId(claims))
                        .header(ROLE_HEADER, jwtUtil.extractRole(claims))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (JwtTokenExpiredException e) {
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Token has expired");
            } catch (Exception e) {
                log.error("JWT validation error: {}", e.getMessage());
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        };
    }

    private Mono<Void> handleError(ServerWebExchange exchange, HttpStatus status, String message) {
        log.error("Authentication error: {}", message);
        exchange.getResponse().setStatusCode(status);

        // Create error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", exchange.getRequest().getPath().value());

        byte[] bytes = null;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            log.error("Error creating error response", e);
        }

        if (bytes != null) {
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        return exchange.getResponse().setComplete();
    }

    private boolean isPublicPath(String path) {
        return publicPathsConfig.getPublicPaths().stream()
                .anyMatch(publicPath -> {
                    if (publicPath.endsWith("/**")) {
                        String prefix = publicPath.substring(0, publicPath.length() - 3);
                        return path.startsWith(prefix);
                    }
                    return path.equals(publicPath);
                });
    }

    @Data
    public static class Config {
    }
}