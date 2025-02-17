package com.pdc.gatewayservice.utils;

import com.pdc.gatewayservice.exceptions.InvalidTokenException;
import com.pdc.gatewayservice.exceptions.JwtTokenExpiredException;
import com.pdc.gatewayservice.services.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final Key key;
    private final TokenBlacklistService blacklistService;

    public JwtUtil(@Value("${jwt.access.secret}") String secret, TokenBlacklistService blacklistService) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.blacklistService = blacklistService;
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new JwtTokenExpiredException("JWT token has expired");
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    public boolean validateToken(String token) {
        try {
            // First check if token is blacklisted
            if (blacklistService.isBlacklisted(token)) {
                log.debug("Token is blacklisted");
                return false;
            }

            // Then verify the JWT signature and expiration
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUserId(Claims claims) {
        return claims.getSubject();
    }

    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }
}