package edu.manipal.cse.authservice.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class AuthenticationException extends RuntimeException {
    private final List<String> errors;

    public AuthenticationException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }

    public AuthenticationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}