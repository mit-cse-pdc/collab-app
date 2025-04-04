package edu.manipal.cse.lectureservicereactive.exceptions;

public class OperationFailedException extends RuntimeException {
    public OperationFailedException(String message) {
        super(message);
    }
    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}