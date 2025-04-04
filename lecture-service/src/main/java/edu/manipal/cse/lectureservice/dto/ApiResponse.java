package edu.manipal.cse.lectureservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private int status;
    private String message;
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Static factory method for success response
    public static <T> ApiResponse<T> createSuccess(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Static factory method for single error response
    public static <T> ApiResponse<T> createSingleError(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

//    // Static factory method for validation errors response
//    public static <T> ApiResponse<T> createValidationError(List<ErrorDetail> errors, String message, int status) {
//        return ApiResponse.<T>builder()
//                .success(false)
//                .status(status)
//                .message(message)
//                .data(null)
//                .errors(errors)
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
}