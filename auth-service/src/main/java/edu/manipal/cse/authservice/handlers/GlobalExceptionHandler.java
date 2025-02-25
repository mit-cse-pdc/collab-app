package edu.manipal.cse.authservice.handlers;

import edu.manipal.cse.authservice.dto.response.ApiResponse;
import edu.manipal.cse.authservice.dto.response.ErrorDetail;
import edu.manipal.cse.authservice.exceptions.AuthenticationException;
import edu.manipal.cse.authservice.exceptions.InvalidTokenException;
import edu.manipal.cse.authservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.authservice.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("credentials", ex.getMessage())
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.createValidationError(
                        errors,
                        "Authentication failed",
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(
            InvalidTokenException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("token", "The provided authentication token is invalid")
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.createValidationError(
                        errors,
                        "Invalid authentication token",
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenExpiredException(
            TokenExpiredException ex) {
        List<ErrorDetail> errors = Collections.singletonList(
                new ErrorDetail("token", "Your authentication token has expired. Please log in again")
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.createValidationError(
                        errors,
                        "Token expired",
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.createSingleError(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        List<ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.createValidationError(
                        errors,
                        "Invalid request data",
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.createSingleError(
                        "Something went wrong. Please try again later",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
}