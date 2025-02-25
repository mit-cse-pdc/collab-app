package edu.manipal.cse.questionbankservice.dto.request;

import edu.manipal.cse.questionbankservice.entities.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Schema(description = "Create Question Request")
@Data
@Builder
public class CreateQuestionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Chapter ID the question belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Chapter ID is required")
    private UUID chapterId;

    @Schema(description = "Faculty ID creating the question", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Faculty ID is required")
    private UUID facultyId;

    @Schema(description = "Title of the question", example = "Geography Question 1")
    @NotBlank(message = "Question title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Schema(description = "Text content of the question", example = "What is the capital of France?")
    @NotBlank(message = "Question text is required")
    private String text;

    @Schema(description = "Type of question", example = "MULTIPLE_CHOICE")
    @NotNull(message = "Question type is required")
    private Question.QuestionType questionType;

    @Schema(description = "List of possible answers")
    @NotEmpty(message = "At least one answer is required")
    private List<CreateAnswerRequest> answers;
}
