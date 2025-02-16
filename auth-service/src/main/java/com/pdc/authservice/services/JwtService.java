package com.pdc.authservice.services;

import com.pdc.authservice.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Getter
@Slf4j
public class JwtService {
    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    public void init() {
        try {
            this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
            this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
            log.info("JWT keys initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT keys", e);
            throw new IllegalStateException("Failed to initialize JWT keys", e);
        }
    }

    public String generateAccessToken(String userId, String role) {
        return generateToken(userId, Map.of("role", role), accessKey, accessExpiration);
    }

    public String generateRefreshToken(String userId, String role) {
        return generateToken(userId, Map.of("role", role), refreshKey, refreshExpiration);
    }

    private String generateToken(String userId, Map<String, Object> claims, Key key, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    public String validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    private String validateToken(String token, Key key) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                throw new InvalidTokenException("Token has expired");
            }

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    public String extractRoleFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token, refreshKey);
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Error extracting role from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    public Claims extractAllClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    public Long getAccessTokenExpiration() {
        return this.accessExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return this.refreshExpiration;
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration, refreshKey).before(new Date());
    }
}
