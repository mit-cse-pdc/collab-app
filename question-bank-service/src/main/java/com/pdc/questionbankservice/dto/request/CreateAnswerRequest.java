package com.pdc.questionbankservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Create Answer Request")
@Data
@Builder
public class CreateAnswerRequest {
    @Schema(description = "Answer text", example = "Paris")
    @NotBlank(message = "Answer text is required")
    private String text;

    @Schema(description = "Whether this answer is correct", example = "true")
    @NotNull(message = "Is correct flag is required")
    private Boolean isCorrect;

    @Schema(description = "Explanation for the answer", example = "Paris is the capital city of France")
    private String explanation;
}

