package com.pdc.questionbankservice.dto.request;

import com.pdc.questionbankservice.entities.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Question Search Request")
@Data
@Builder
public class QuestionSearchRequest {
    @Schema(description = "Chapter ID filter", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID chapterId;

    @Schema(description = "Faculty ID filter", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID facultyId;

    @Schema(description = "Question type filter", example = "MULTIPLE_CHOICE")
    private Question.QuestionType questionType;

    @Schema(description = "Search term for questions", example = "capital")
    private String searchTerm;
}

