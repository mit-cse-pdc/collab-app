package com.pdc.authservice.handlers;

import com.pdc.authservice.dto.response.ApiResponse;
import com.pdc.authservice.dto.response.ErrorDetail;
import com.pdc.authservice.exceptions.AuthenticationException;
import com.pdc.authservice.exceptions.InvalidTokenException;
import com.pdc.authservice.exceptions.ResourceNotFoundException;
import com.pdc.authservice.exceptions.TokenExpiredException;
import com.pdc.authservice.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("credentials", ex.getMessage())
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        "Authentication failed",
                        errors,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(
            InvalidTokenException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("token", "The provided authentication token is invalid")
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        "Invalid authentication token",
                        errors,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpiredException(
            TokenExpiredException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("token", "Your authentication token has expired. Please log in again")
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        "Token expired",
                        errors,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("resource", ex.getMessage())
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        "Resource not found",
                        errors,
                        HttpStatus.NOT_FOUND
                ));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        List<ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(
                        "Invalid request data",
                        errors,
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.error(
                        "Something went wrong. Please try again later.",
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
}