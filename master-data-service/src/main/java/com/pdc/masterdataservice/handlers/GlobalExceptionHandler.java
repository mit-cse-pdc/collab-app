package com.pdc.masterdataservice.handlers;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.pdc.masterdataservice.dto.response.ApiResponse;
import com.pdc.masterdataservice.dto.response.ErrorDetail;
import com.pdc.masterdataservice.exceptions.DuplicateResourceException;
import com.pdc.masterdataservice.exceptions.ResourceInUseException;
import com.pdc.masterdataservice.exceptions.ResourceNotFoundException;
import com.pdc.masterdataservice.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        "No records found",
                        null,
                        HttpStatus.NOT_FOUND
                ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseUtil.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.CONFLICT
                ));
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceInUseException(ResourceInUseException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseUtil.error(
                        ex.getMessage(),
                        null,
                        HttpStatus.CONFLICT
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
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
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        List<ErrorDetail> errors = List.of(
                new ErrorDetail(ex.getName(), "Invalid value provided for parameter")
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(
                        "Invalid parameter type",
                        errors,
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = ex.getCause() instanceof UnrecognizedPropertyException cause ?
                String.format("Unknown field: '%s'", cause.getPropertyName()) :
                "Invalid request body";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(
                        message,
                        null,
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        "No records found",
                        null,
                        HttpStatus.NOT_FOUND
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