package edu.manipal.cse.masterdataservice.handlers;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import edu.manipal.cse.masterdataservice.dto.response.ApiResponse;
import edu.manipal.cse.masterdataservice.dto.response.ErrorDetail;
import edu.manipal.cse.masterdataservice.exceptions.DuplicateResourceException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceInUseException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.masterdataservice.utils.ResponseUtil;
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
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND
                ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseUtil.error(
                        ex.getMessage(),
                        HttpStatus.CONFLICT
                ));
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceInUseException(ResourceInUseException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseUtil.error(
                        ex.getMessage(),
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
                .body(ResponseUtil.validationError(
                        errors,
                        "Invalid request data",
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(
                        String.format("Invalid value provided for parameter '%s'", ex.getName()),
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
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        "No records found",
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
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
}