package com.pdc.userservice.utils;

import com.pdc.userservice.dto.response.ApiResponse;
import com.pdc.userservice.dto.response.ErrorDetail;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ResponseUtil {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return new ApiResponse<>(
                true,
                status.value(),
                message,
                data,
                null,
                LocalDateTime.now().format(ISO_FORMATTER)
        );
    }

    public static ApiResponse<Object> error(String message, List<ErrorDetail> errors, HttpStatus status) {
        return new ApiResponse<>(
                false,
                status.value(),
                message,
                null,
                errors,
                LocalDateTime.now().format(ISO_FORMATTER)
        );
    }

    public static List<ErrorDetail> mapValidationErrors(Map<String, String> errors) {
        return errors.entrySet().stream()
                .map(entry -> new ErrorDetail(entry.getKey(), entry.getValue()))
                .toList();
    }
}