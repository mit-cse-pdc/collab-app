package com.pdc.questionbankservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Update Chapter Order Request")
@Data
@Builder
public class UpdateChapterOrderRequest {
    @Schema(description = "Chapter ID to reorder", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Chapter ID is required")
    private UUID chapterId;

    @Schema(description = "New position number for the chapter", example = "2")
    @NotNull(message = "New chapter number is required")
    @Min(value = 1, message = "Chapter number must be at least 1")
    private Integer newChapterNo;
}