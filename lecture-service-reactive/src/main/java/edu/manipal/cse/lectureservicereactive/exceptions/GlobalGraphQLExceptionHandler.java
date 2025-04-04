package edu.manipal.cse.lectureservicereactive.exceptions;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GlobalGraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    private static final Logger log = LoggerFactory.getLogger(GlobalGraphQLExceptionHandler.class);

    @Override
    protected GraphQLError resolveToSingleError(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
        log.debug("Resolving exception: {}", ex.getClass().getSimpleName());

        // 1. Custom Exceptions implementing GraphQLError (e.g., ResourceNotFoundException)
        if (ex instanceof GraphQLError gqlError && !(ex instanceof ConstraintViolationException)) {
            log.warn("Handling custom GraphQLError: type={}, message={}", gqlError.getErrorType(), gqlError.getMessage());
            return gqlError;
        }

        // 2. Bean Validation Errors (ConstraintViolationException)
        if (ex instanceof ConstraintViolationException cve) {
            log.warn("Constraint violation detected: {}", cve.getMessage());
            ConstraintViolation<?> violation = cve.getConstraintViolations().iterator().next();
            String field = violation.getPropertyPath().toString();
            String message = String.format("Input validation failed for '%s': %s", field, violation.getMessage());

            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(message)
                    .extensions(Map.of(
                            "field", field,
                            "invalidValue", String.valueOf(violation.getInvalidValue()), // Be careful exposing sensitive values
                            "errorCode", "VALIDATION_ERROR"
                    ))
                    .build();
        }

        // 3. Spring Binding/Type Mismatch Errors
        if (ex instanceof BindException || ex instanceof TypeMismatchException) {
            log.warn("Binding or type mismatch error: {}", ex.getMessage());
            String field = (ex instanceof BindException be) && be.getFieldError() != null ? be.getFieldError().getField() : "unknown";
            String specificMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            String message = String.format("Invalid value provided for field '%s'. Please check the expected type or format.", field);


            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(message)
                    .extensions(Map.of(
                            "field", field,
                            "details", specificMessage, // Keep specific message for logs/debugging, maybe not always for client
                            "errorCode", "BINDING_ERROR"
                    ))
                    .build();
        }

        // 4. Handle Common Argument/Coercion Issues (Often wrapped in RuntimeException or IllegalArgumentException)
        if (ex instanceof IllegalArgumentException || (ex.getCause() instanceof IllegalArgumentException)) {
            log.warn("Illegal argument detected: {}", ex.getMessage());
            String message = "Invalid argument provided. Please check the value and type requirements.";

            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(message)
                    .extensions(Map.of("details", ex.getMessage(),"errorCode", "INVALID_ARGUMENT"))
                    .build();
        }


        // 5. Generic Fallback Handler (Internal Server Error)
        log.error("Unhandled internal error during GraphQL execution: path={}", env.getExecutionStepInfo().getPath(), ex);
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected error occurred processing your request.") // User-friendly generic message
                .extensions(Map.of("errorCode", "INTERNAL_SERVER_ERROR"))
                .build();
    }

    // Override for multiple errors from ConstraintViolationException ---
    @Override
    protected List<GraphQLError> resolveToMultipleErrors(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
        if (ex instanceof ConstraintViolationException cve) {
            log.warn("Constraint violation detected with multiple errors: {}", cve.getMessage());
            return cve.getConstraintViolations().stream()
                    .map(violation -> {
                        String field = violation.getPropertyPath().toString();
                        String message = String.format("Input validation failed for '%s': %s", field, violation.getMessage());
                        return GraphqlErrorBuilder.newError(env)
                                .errorType(ErrorType.BAD_REQUEST)
                                .message(message)
                                .extensions(Map.of(
                                        "field", field,
                                        "invalidValue", String.valueOf(violation.getInvalidValue()), // Use with caution
                                        "errorCode", "VALIDATION_ERROR"
                                ))
                                .build();
                    })
                    .collect(Collectors.toList());
        }
        GraphQLError singleError = resolveToSingleError(ex, env);
        return singleError != null ? List.of(singleError) : List.of(); // Return empty list if null
    }
}
