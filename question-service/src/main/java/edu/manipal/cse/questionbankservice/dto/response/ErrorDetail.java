package edu.manipal.cse.questionbankservice.dto.response;

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

    // Constructor for generic errors (no specific field)
    public ErrorDetail(String message) {
        this.field = "error";  // Default field name instead of null
        this.message = message;
    }
}