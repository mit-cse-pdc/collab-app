package edu.manipal.cse.masterdataservice.exceptions;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}