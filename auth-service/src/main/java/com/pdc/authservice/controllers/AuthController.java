package com.pdc.authservice.controllers;

import com.pdc.authservice.dto.request.LoginRequest;
import com.pdc.authservice.dto.request.RefreshTokenRequest;
import com.pdc.authservice.dto.response.ApiResponse;
import com.pdc.authservice.dto.response.TokenResponse;
import com.pdc.authservice.services.AuthService;
import com.pdc.authservice.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for students and faculty members")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Student login",
            description = "Authenticates a student using email and password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 200,
                        "message": "Login successful",
                        "data": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "userId": "123e4567-e89b-12d3-a456-426614174000"
                        },
                        "errors": null,
                        "timestamp": "2025-02-17T14:25:00Z"
                    }
                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "status": 401,
                        "message": "Authentication failed",
                        "data": null,
                        "errors": [
                            {
                                "field": "credentials",
                                "message": "Invalid email or password"
                            }
                        ],
                        "timestamp": "2025-02-17T14:25:00Z"
                    }
                    """)
                    )
            )
    })
    @PostMapping("/student/login")
    public ResponseEntity<ApiResponse<TokenResponse>> studentLogin(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.studentLogin(request);
        return ResponseEntity.ok(
                ResponseUtil.success(response, "Login successful", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Faculty login",
            description = "Authenticates a faculty member using email and password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 200,
                        "message": "Login successful",
                        "data": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "userId": "123e4567-e89b-12d3-a456-426614174000"
                        },
                        "errors": null,
                        "timestamp": "2025-02-17T14:25:00Z"
                    }
                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "status": 401,
                        "message": "Authentication failed",
                        "data": null,
                        "errors": [
                            {
                                "field": "credentials",
                                "message": "Invalid email or password for faculty login"
                            }
                        ],
                        "timestamp": "2025-02-17T14:25:00Z"
                    }
                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Faculty not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "status": 404,
                        "message": "Resource not found",
                        "data": null,
                        "errors": [
                            {
                                "field": "email",
                                "message": "No faculty account found with the provided email"
                            }
                        ],
                        "timestamp": "2025-02-17T14:25:00Z"
                    }
                    """)
                    )
            )
    })
    @PostMapping("/faculty/login")
    public ResponseEntity<ApiResponse<TokenResponse>> facultyLogin(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.facultyLogin(request);
        return ResponseEntity.ok(
                ResponseUtil.success(response, "Login successful", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Refresh token",
            description = "Generates new access and refresh tokens using a valid refresh token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid refresh token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(
                ResponseUtil.success(response, "Token refreshed successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Logout",
            description = "Invalidates the current access token and all refresh tokens for the user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged out",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(
            @Parameter(description = "Bearer token", required = true)
            @RequestHeader("Authorization") String token) {
        Boolean result = authService.logout(token);
        return ResponseEntity.ok(
                ResponseUtil.success(result, "Logged out successfully", HttpStatus.OK)
        );
    }
}