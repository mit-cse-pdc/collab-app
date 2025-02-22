package com.pdc.userservice.handlers;

import com.pdc.userservice.dto.response.ApiResponse;
import com.pdc.userservice.dto.response.ErrorDetail;
import com.pdc.userservice.utils.ResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        "No records found",
                        null,
                        HttpStatus.NOT_FOUND
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        List<ErrorDetail> errors = List.of(new ErrorDetail(null, ex.getMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(
                        "Invalid request data",
                        errors,
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errors = ex.getBindingResult().getFieldErrors()
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        List<ErrorDetail> errors = List.of(
                new ErrorDetail(ex.getName(), "Invalid value: " + ex.getValue())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(
                        "Invalid request data",
                        errors,
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        List<ErrorDetail> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String fieldName = violation.getPropertyPath().toString();
                    fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
                    return new ErrorDetail(fieldName, violation.getMessage());
                })
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
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.error(
                        "Something went wrong. Please try again later.",
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
}