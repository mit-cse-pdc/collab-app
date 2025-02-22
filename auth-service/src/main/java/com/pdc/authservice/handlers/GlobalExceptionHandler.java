package com.pdc.authservice.handlers;

import com.pdc.authservice.dto.response.ApiResponse;
import com.pdc.authservice.dto.response.ErrorDetail;
import com.pdc.authservice.exceptions.*;
import com.pdc.authservice.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        "Unauthorized. Please log in.",
                        null,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(
            InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpiredException(
            TokenExpiredException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenRevokedException(
            TokenRevokedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtil.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        "No records found",
                        null,
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