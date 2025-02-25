package edu.manipal.cse.authservice.services.impl;

import edu.manipal.cse.authservice.clients.UserClient;
import edu.manipal.cse.authservice.dto.FacultyDTO;
import edu.manipal.cse.authservice.dto.StudentDTO;
import edu.manipal.cse.authservice.dto.request.LoginRequest;
import edu.manipal.cse.authservice.dto.request.RefreshTokenRequest;
import edu.manipal.cse.authservice.dto.response.ApiResponse;
import edu.manipal.cse.authservice.dto.response.TokenResponse;
import edu.manipal.cse.authservice.entities.RefreshToken;
import edu.manipal.cse.authservice.exceptions.AuthenticationException;
import edu.manipal.cse.authservice.exceptions.InvalidTokenException;
import edu.manipal.cse.authservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.authservice.services.AuthService;
import edu.manipal.cse.authservice.services.JwtService;
import edu.manipal.cse.authservice.services.RefreshTokenService;
import edu.manipal.cse.authservice.services.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private static final String ROLE_STUDENT = "ROLE_STUDENT";
    private static final String ROLE_FACULTY = "ROLE_FACULTY";
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserClient userClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public TokenResponse studentLogin(LoginRequest request) {
        log.info("Processing student login for email: {}", request.getEmail());

        ResponseEntity<ApiResponse<StudentDTO>> response = userClient.getStudentByEmail(request.getEmail());
        if (response == null || response.getBody() == null || response.getBody().getData() == null) {
            throw new ResourceNotFoundException("No student account found with the provided email");
        }

        StudentDTO student = response.getBody().getData();
        String hashedPassword = student.getPassword();

        if (hashedPassword == null || !passwordEncoder.matches(request.getPassword(), hashedPassword)) {
            throw new AuthenticationException("Invalid email or password for student login");
        }

        return generateTokens(student.getStudentId(), ROLE_STUDENT);
    }

    @Override
    @Transactional
    public TokenResponse facultyLogin(LoginRequest request) {
        log.info("Processing faculty login for email: {}", request.getEmail());

        ResponseEntity<ApiResponse<FacultyDTO>> response = userClient.getFacultyByEmail(request.getEmail());
        if (response == null || response.getBody() == null || response.getBody().getData() == null) {
            throw new ResourceNotFoundException("No faculty account found with the provided email");
        }

        FacultyDTO faculty = response.getBody().getData();
        String hashedPassword = faculty.getPassword();

        if (hashedPassword == null || !passwordEncoder.matches(request.getPassword(), hashedPassword)) {
            throw new AuthenticationException("Invalid email or password for faculty login");
        }

        return generateTokens(faculty.getFacultyId(), ROLE_FACULTY);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            UUID userId = refreshToken.getUserId();
            String role = refreshToken.getRole();

            //revoke refresh token
            refreshTokenService.revokeRefreshToken(request.getRefreshToken());
            // generate new refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userId, role);
            // Generate new access token
            String accessToken = jwtService.generateAccessToken(userId.toString(), role);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .userId(userId.toString())
                    .build();

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

    @Override
    @Transactional
    public Boolean logout(String token) {
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Invalid token format");
        }

        try {
            String accessToken = token.substring(BEARER_PREFIX.length());
            String userId = jwtService.validateAccessToken(accessToken);

            // Execute token blacklisting and refresh token revocation in parallel
            long remainingValidityTime = jwtService.getTokenRemainingValidityInMillis(accessToken);
            tokenBlacklistService.blacklistToken(accessToken, remainingValidityTime);
            refreshTokenService.revokeAllUserTokens(UUID.fromString(userId));

            log.info("Logout successful for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            throw new InvalidTokenException("Invalid access token");
        }
    }

    private TokenResponse generateTokens(UUID userId, String role) {
        String accessToken = jwtService.generateAccessToken(userId.toString(), role);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId, role);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(userId.toString())
                .build();
    }
}