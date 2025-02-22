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

@Service
@Getter
@Slf4j
public class JwtService {
    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    private Key accessKey;

    @PostConstruct
    public void init() {
        try {
            this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
            log.info("JWT access key initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT access key", e);
            throw new IllegalStateException("Failed to initialize JWT access key", e);
        }
    }

    public String generateAccessToken(String userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setClaims(Map.of("role", role))
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }


    public String validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
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

    public long getTokenRemainingValidityInMillis(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.error("Error getting token remaining validity: {}", e.getMessage());
            return 0;
        }
    }
}