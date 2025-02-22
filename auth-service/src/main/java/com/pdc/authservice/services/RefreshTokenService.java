package com.pdc.authservice.services;

import com.pdc.authservice.entities.RefreshToken;
import com.pdc.authservice.exceptions.InvalidTokenException;
import com.pdc.authservice.exceptions.TokenExpiredException;
import com.pdc.authservice.exceptions.TokenRevokedException;
import com.pdc.authservice.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(UUID userId, String role) {
        log.debug("Creating refresh token for user: {} with role: {}", userId, role);

        // Revoke existing tokens
        refreshTokenRepository.revokeAllUserTokens(userId);

        // Create new token
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .role(role)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000))
                .build();

        //save token in DB
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Created new refresh token for user: {} with role: {}", userId, role);
        return savedToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        log.debug("Verifying refresh token");

        return refreshTokenRepository.findByToken(token)
                .map(this::validateRefreshToken)
                .orElseThrow(() -> {
                    log.error("Refresh token not found: {}", token);
                    return new InvalidTokenException("Refresh token not found");
                });
    }

    private RefreshToken validateRefreshToken(RefreshToken token) {
        if (token.isRevoked()) {
            log.error("Refresh token has been revoked: {}", token.getToken());
            throw new TokenRevokedException("Refresh token has been revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Refresh token has expired: {}", token.getToken());
            throw new TokenExpiredException("Refresh token has expired");
        }

        return token;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        log.debug("Revoking refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Token not found for revocation: {}", token);
                    return new InvalidTokenException("Refresh token not found");
                });

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token revoked: {}", token);
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        log.debug("Revoking all tokens for user: {}", userId);
        refreshTokenRepository.revokeAllUserTokens(userId);
        log.info("All tokens revoked for user: {}", userId);
    }
}