package com.pdc.authservice.services.impl;

import com.pdc.authservice.clients.UserClient;
import com.pdc.authservice.dto.FacultyDTO;
import com.pdc.authservice.dto.StudentDTO;
import com.pdc.authservice.dto.request.LoginRequest;
import com.pdc.authservice.dto.request.RefreshTokenRequest;
import com.pdc.authservice.dto.response.ApiResponse;
import com.pdc.authservice.dto.response.TokenResponse;
import com.pdc.authservice.entities.RefreshToken;
import com.pdc.authservice.exceptions.AuthenticationException;
import com.pdc.authservice.exceptions.InvalidTokenException;
import com.pdc.authservice.exceptions.ResourceNotFoundException;
import com.pdc.authservice.services.AuthService;
import com.pdc.authservice.services.JwtService;
import com.pdc.authservice.services.RefreshTokenService;
import com.pdc.authservice.services.TokenBlacklistService;
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
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public TokenResponse studentLogin(LoginRequest request) {
        return loginUser(
                request.getEmail(),
                request.getPassword(),
                userClient::getStudentByEmail,
                StudentDTO::getStudentId,
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
                FacultyDTO::getFacultyId,
                "ROLE_FACULTY"
        );
    }

    private <T> TokenResponse loginUser(
            String email,
            String password,
            Function<String, ResponseEntity<ApiResponse<T>>> userFetcher,
            Function<T, UUID> userIdExtractor,
            String role
    ) {
        log.info("Processing login request for email: {}", email);

        ResponseEntity<ApiResponse<T>> response = userFetcher.apply(email);

        if (response == null || response.getBody() == null || response.getBody().getData() == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        T user = response.getBody().getData();
        String hashedPassword = getPassword(user);

        log.debug("Retrieved user data - Email: {}, HashedPassword present: {}",
                email,
                hashedPassword != null && !hashedPassword.isEmpty());

        if (hashedPassword == null || hashedPassword.isEmpty()) {
            log.error("Empty hashed password received from user-service for email: {}", email);
            throw new AuthenticationException("Invalid user data received");
        }

        if (!passwordEncoder.matches(password, hashedPassword)) {
            log.error("Password mismatch for user: {}", email);
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

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        log.info("Processing refresh token request");

        try {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            log.debug("Found valid refresh token for user: {}", refreshToken.getUserId());

            String role;
            try {
                ResponseEntity<ApiResponse<FacultyDTO>> facultyResponse = userClient.getFacultyById(refreshToken.getUserId());
                if (facultyResponse.getBody() != null && facultyResponse.getBody().getData() != null) {
                    role = "ROLE_FACULTY";
                } else {
                    ResponseEntity<ApiResponse<StudentDTO>> studentResponse = userClient.getStudentById(refreshToken.getUserId());
                    if (studentResponse.getBody() != null && studentResponse.getBody().getData() != null) {
                        role = "ROLE_STUDENT";
                    } else {
                        throw new ResourceNotFoundException("User not found");
                    }
                }
            } catch (Exception e) {
                throw new ResourceNotFoundException("User not found");
            }

            String accessToken = jwtService.generateAccessToken(refreshToken.getUserId().toString(), role);
            refreshTokenService.revokeRefreshToken(request.getRefreshToken());
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
        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format");
        }

        try {
            String accessToken = token.substring(7);
            String userId = jwtService.validateAccessToken(accessToken);
            long remainingValidityTime = jwtService.getTokenRemainingValidityInMillis(accessToken);

            tokenBlacklistService.blacklistToken(accessToken, remainingValidityTime);
            refreshTokenService.revokeAllUserTokens(UUID.fromString(userId));

            log.info("Logout successful for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new InvalidTokenException("Invalid access token");
        }
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