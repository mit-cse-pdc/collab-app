package edu.manipal.cse.masterdataservice.utils;

import edu.manipal.cse.masterdataservice.dto.response.ApiResponse;
import edu.manipal.cse.masterdataservice.dto.response.ErrorDetail;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ResponseUtil {
    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return ApiResponse.createSuccess(data, message, status.value());
    }

    public static <T> ApiResponse<T> error(String errorMessage, HttpStatus status) {
        return ApiResponse.createSingleError(errorMessage, status.value());
    }

    public static <T> ApiResponse<T> validationError(List<ErrorDetail> errors, String message, HttpStatus status) {
        return ApiResponse.createValidationError(errors, message, status.value());
    }
}