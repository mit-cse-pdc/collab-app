package com.pdc.gatewayservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for JWT token operations
 * Handles token validation and claims extraction
 */
@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts all claims from the JWT token
     * @param token JWT token
     * @return Claims object containing token claims
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates the JWT token
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the user ID from JWT token
     * @param claims JWT claims
     * @return user ID as string
     */
    public String extractUserId(Claims claims) {
        return claims.get("userId", String.class);
    }

    /**
     * Extracts the user role from JWT token
     * @param claims JWT claims
     * @return user role as string
     */
    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }
}