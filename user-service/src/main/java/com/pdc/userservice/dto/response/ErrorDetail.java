package com.pdc.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String field;
    private String message;
}