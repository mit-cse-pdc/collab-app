package edu.manipal.cse.lectureservicereactive.exceptions;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// Re-using ResourceNotFoundException from previous example
public class ResourceNotFoundException extends RuntimeException implements GraphQLError {
    private final String resourceName;
    private final String resourceId; // Keep as String for flexibility

    public ResourceNotFoundException(String resourceName, UUID resourceId) {
        this(resourceName, resourceId != null ? resourceId.toString() : "null");
    }
    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(String.format("%s not found with ID: %s", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    @Override public List<SourceLocation> getLocations() { return null; }
    @Override public ErrorClassification getErrorType() { return graphql.ErrorType.DataFetchingException; } // Or NOT_FOUND classification
    @Override public String getMessage() { return super.getMessage(); } // Ensure message is accessible

    @Override
    public Map<String, Object> getExtensions() {
        return Map.of(
                "resource", resourceName,
                "resourceId", resourceId,
                "errorCode", "RESOURCE_NOT_FOUND"
        );
    }
}