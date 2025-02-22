package com.pdc.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private int status;
    private String message;
    private T data;
    private List<ErrorDetail> errors = new ArrayList<>();
    private String timestamp;
}
