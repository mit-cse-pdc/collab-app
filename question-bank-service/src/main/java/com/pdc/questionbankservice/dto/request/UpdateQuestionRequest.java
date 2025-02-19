package com.pdc.questionbankservice.dto.request;

import com.pdc.questionbankservice.entities.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "Update Question Request")
@Data
@Builder
public class UpdateQuestionRequest {
    @Schema(description = "Updated title of the question", example = "Geography Question 1 - Revised")
    @NotBlank(message = "Question title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Schema(description = "Updated text content of the question", example = "What is the capital of Spain?")
    @NotBlank(message = "Question text is required")
    private String text;

    @Schema(description = "Updated question type", example = "MULTIPLE_CHOICE")
    @NotNull(message = "Question type is required")
    private Question.QuestionType questionType;

    @Schema(description = "Updated list of possible answers")
    @NotEmpty(message = "At least one answer is required")
    private List<CreateAnswerRequest> answers;
}