package edu.manipal.cse.gatewayservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.manipal.cse.gatewayservice.exceptions.JwtTokenExpiredException;
import edu.manipal.cse.gatewayservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String ROLE_HEADER = "X-User-Role";
    private static final String GRAPHQL_PATH = "/lectures/graphql";

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
                log.trace("Public path detected, skipping JWT validation for: {}", path);
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                if (isWebSocketUpgradeRequest(request)) {
                    log.warn("WebSocket upgrade request denied: Missing or invalid Authorization header for path {}", path);
                    return handleError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid authorization header for WebSocket connection");
                }
                log.warn("HTTP request denied: Missing or invalid Authorization header for path {}", path);
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid authorization header");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("JWT validation failed (invalid/expired) for path {}", path);
                    return handleError(exchange, HttpStatus.UNAUTHORIZED, "Token is expired or invalid");
                }

                Claims claims = jwtUtil.extractClaims(token);
                String userId = jwtUtil.extractUserId(claims);
                String role = jwtUtil.extractRole(claims);

                if (isWebSocketUpgradeRequest(request)) {
                    log.info("WebSocket upgrade request authenticated for user: {}, role: {}, path: {}", userId, role, path);
                    ServerHttpRequest downstreamRequest = request.mutate()
                            .header(USER_ID_HEADER, userId)
                            .header(ROLE_HEADER, role)
                            .build();
                    return chain.filter(exchange.mutate().request(downstreamRequest).build());
                } else {
                    log.debug("Forwarding HTTP request with userId: {} and role: {} for path {}", userId, role, path);
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header(USER_ID_HEADER, userId)
                            .header(ROLE_HEADER, role)
                            .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }

            } catch (JwtTokenExpiredException e) {
                log.warn("JWT validation failed (expired) for path {}: {}", path, e.getMessage());
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Token has expired");
            } catch (Exception e) {
                log.error("JWT validation error for path {}: {}", path, e.getMessage(), e);
                return handleError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token processing error");
            }
        };
    }

    private boolean isWebSocketUpgradeRequest(ServerHttpRequest request) {
        return "websocket".equalsIgnoreCase(request.getHeaders().getUpgrade()) &&
                request.getHeaders().getConnection().stream()
                        .anyMatch(h -> h.equalsIgnoreCase("upgrade"));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot write error body.");
            return Mono.empty();
        }

        log.error("Authentication/Authorization Error: Status={}, Message={}, Path={}", status, message, exchange.getRequest().getPath().value());
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", exchange.getRequest().getPath().value());

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error writing error response body", e);
            return response.setComplete();
        }
    }

    private boolean isPublicPath(String path) {
        if (publicPathsConfig == null || publicPathsConfig.getPublicPaths() == null) {
            log.warn("PublicPathsConfig or its path list is null. Treating path '{}' as non-public.", path);
            return false;
        }
        return publicPathsConfig.getPublicPaths().stream()
                .filter(Objects::nonNull)
                .anyMatch(publicPath -> {
                    if (publicPath.endsWith("/**")) {
                        String prefix = publicPath.substring(0, publicPath.length() - 3);
                        if (prefix.isEmpty() || prefix.equals("/")) return true;
                        return path.startsWith(prefix);
                    }
                    return path.equals(publicPath);
                });
    }

    @Data
    public static class Config {
    }
}
