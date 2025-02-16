package com.pdc.authservice.services.impl;

import com.pdc.authservice.clients.UserClient;
import com.pdc.authservice.dto.FacultyDTO;
import com.pdc.authservice.dto.StudentDTO;
import com.pdc.authservice.dto.request.LoginRequest;
import com.pdc.authservice.dto.request.RefreshTokenRequest;
import com.pdc.authservice.dto.response.TokenResponse;
import com.pdc.authservice.entities.RefreshToken;
import com.pdc.authservice.exceptions.AuthenticationException;
import com.pdc.authservice.exceptions.InvalidTokenException;
import com.pdc.authservice.exceptions.ResourceNotFoundException;
import com.pdc.authservice.services.AuthService;
import com.pdc.authservice.services.JwtService;
import com.pdc.authservice.services.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserClient userClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public TokenResponse studentLogin(LoginRequest request) {
        return loginUser(
                request.getEmail(),
                request.getPassword(),
                userClient::getStudentByEmail,
                user -> ((StudentDTO) user).getStudentId(),
                "ROLE_STUDENT"
        );
    }

    @Override
    @Transactional
    public TokenResponse facultyLogin(LoginRequest request) {
        return loginUser(
                request.getEmail(),
                request.getPassword(),
                userClient::getFacultyByEmail,
                user -> ((FacultyDTO) user).getFacultyId(),
                "ROLE_FACULTY"
        );
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        log.info("Processing refresh token request");

        try {
            // 1. Verify refresh token from database
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            log.debug("Found valid refresh token for user: {}", refreshToken.getUserId());

            // 2. Get user details to determine role
            String role;
            try {
                userClient.getFacultyById(refreshToken.getUserId());
                role = "ROLE_FACULTY";
            } catch (Exception e) {
                try {
                    userClient.getStudentById(refreshToken.getUserId());
                    role = "ROLE_STUDENT";
                } catch (Exception ex) {
                    throw new ResourceNotFoundException("User not found");
                }
            }

            // 3. Generate new tokens
            String accessToken = jwtService.generateAccessToken(refreshToken.getUserId().toString(), role);

            // 4. Revoke old refresh token
            refreshTokenService.revokeRefreshToken(request.getRefreshToken());

            // 5. Create new refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(refreshToken.getUserId());

            log.info("Token refresh successful for user: {}", refreshToken.getUserId());
            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .userId(refreshToken.getUserId().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public Boolean logout(String token) {
        log.debug("Processing logout request with token: {}", token);

        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format");
        }

        try {
            String accessToken = token.substring(7);

            // Extract userId from the token without validation
            Claims claims = jwtService.extractAllClaims(accessToken, jwtService.getAccessKey());
            String userId = claims.getSubject();

            if (userId == null) {
                throw new InvalidTokenException("Invalid token: no user ID found");
            }

            // Revoke all refresh tokens for the user
            refreshTokenService.revokeAllUserTokens(UUID.fromString(userId));

            log.info("Logout successful for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    private TokenResponse loginUser(
            String email,
            String password,
            Function<String, ResponseEntity<?>> userFetcher,
            Function<Object, UUID> userIdExtractor,
            String role
    ) {
        log.info("Processing login request for email: {}", email);

        ResponseEntity<?> response = userFetcher.apply(email);
        Object user = response.getBody();

        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        if (!passwordEncoder.matches(password, getPassword(user))) {
            throw new AuthenticationException("Invalid credentials");
        }

        UUID userId = userIdExtractor.apply(user);
        String accessToken = jwtService.generateAccessToken(userId.toString(), role);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

        log.info("Login successful for user ID: {}", userId);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(userId.toString())
                .build();
    }

    private String getPassword(Object user) {
        if (user instanceof StudentDTO) {
            return ((StudentDTO) user).getPassword();
        } else if (user instanceof FacultyDTO) {
            return ((FacultyDTO) user).getPassword();
        }
        throw new IllegalArgumentException("Invalid user type");
    }
}