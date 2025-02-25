package edu.manipal.cse.questionbankservice.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Schema(description = "Bulk Question Creation Request")
@Data
@Builder
public class BulkQuestionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Chapter ID for the questions", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Chapter ID is required")
    private UUID chapterId;

    @Schema(description = "Faculty ID creating the questions", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Faculty ID is required")
    private UUID facultyId;

    @Schema(description = "List of questions to create")
    @NotEmpty(message = "Questions list cannot be empty")
    private List<@Valid CreateQuestionRequest> questions;
}
