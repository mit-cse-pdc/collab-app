package edu.manipal.cse.questionbankservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "Update Chapter Request")
@Data
@Builder
public class UpdateChapterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Updated name of the chapter", example = "Chapter 1: Advanced Topics")
    @NotBlank(message = "Chapter name is required")
    @Size(min = 3, max = 255, message = "Chapter name must be between 3 and 255 characters")
    private String name;

    @Schema(description = "Updated description of the chapter", example = "This chapter covers advanced concepts")
    private String description;
}

