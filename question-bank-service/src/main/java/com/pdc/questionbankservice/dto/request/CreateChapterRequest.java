package com.pdc.questionbankservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Create Chapter Request")
@Data
@Builder
public class CreateChapterRequest {
    @Schema(description = "Course ID the chapter belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @Schema(description = "Chapter number", example = "1")
    @NotNull(message = "Chapter number is required")
    @Min(1)
    private Integer chapterNo;

    @Schema(description = "Name of the chapter", example = "Chapter 1: Introduction")
    @NotBlank(message = "Chapter name is required")
    @Size(min = 3, max = 255, message = "Chapter name must be between 3 and 255 characters")
    private String name;

    @Schema(description = "Description of the chapter", example = "This chapter covers basic concepts")
    private String description;
}
