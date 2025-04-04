package edu.manipal.cse.lectureservicereactive.dto.payloads;

import edu.manipal.cse.lectureservicereactive.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PayloadHelper {

    public static List<UserError> mapExceptionToUserErrors(Throwable e) {
        if (e instanceof ResourceNotFoundException rnfe) {
            String field = rnfe.getExtensions() != null ? rnfe.getExtensions().getOrDefault("resourceId", "id").toString() : "id";
            return List.of(new UserError(field, rnfe.getMessage()));
        } else if (e instanceof ConstraintViolationException cve) {
            return cve.getConstraintViolations().stream()
                    .map(cv -> new UserError(cv.getPropertyPath().toString(), cv.getMessage()))
                    .collect(Collectors.toList());
        }
        // TODO: Add specific mappings for your other custom exceptions
        return List.of(new UserError("general", "Operation failed due to an unexpected error."));
    }

    public static List<UserError> emptyUserErrors() {
        return Collections.emptyList();
    }
}
