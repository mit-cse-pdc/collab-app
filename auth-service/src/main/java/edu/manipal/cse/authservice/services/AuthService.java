package edu.manipal.cse.authservice.services;

import edu.manipal.cse.authservice.dto.request.LoginRequest;
import edu.manipal.cse.authservice.dto.request.RefreshTokenRequest;
import edu.manipal.cse.authservice.dto.response.TokenResponse;
import edu.manipal.cse.authservice.exceptions.AuthenticationException;
import edu.manipal.cse.authservice.exceptions.InvalidTokenException;
import edu.manipal.cse.authservice.exceptions.ResourceNotFoundException;
import feign.FeignException;

public interface AuthService {
    /**
     * Authenticates a student using provided credentials and generates access and refresh tokens.
     *
     * @param request LoginRequest containing email/registration number and password
     * @return TokenResponse containing access token, refresh token, and student ID
     * @throws ResourceNotFoundException if student is not found
     * @throws AuthenticationException if credentials are invalid
     * @throws FeignException.FeignClientException if user service is unavailable
     */
    TokenResponse studentLogin(LoginRequest request);

    /**
     * Authenticates a faculty member using provided credentials and generates access and refresh tokens.
     *
     * @param request LoginRequest containing email and password
     * @return TokenResponse containing access token, refresh token, and faculty ID
     * @throws ResourceNotFoundException if faculty member is not found
     * @throws AuthenticationException if credentials are invalid
     * @throws FeignException.FeignClientException if user service is unavailable
     */
    TokenResponse facultyLogin(LoginRequest request);

    /**
     * Generates new access and refresh tokens using a valid refresh token.
     * Invalidates the old refresh token as part of the process.
     *
     * @param request RefreshTokenRequest containing the refresh token
     * @return TokenResponse containing new access token, refresh token, and user ID
     * @throws InvalidTokenException if refresh token is invalid or expired
     */
    TokenResponse refresh(RefreshTokenRequest request);

    /**
     * Invalidates both access and refresh tokens for a user session.
     * Adds the access token to a blacklist and removes the refresh token from storage.
     *
     * @param token Bearer token from Authorization header (format: "Bearer {token}")
     * @return true if logout was successful
     * @throws InvalidTokenException if token format is invalid or token is expired
     */
    Boolean logout(String token);
}