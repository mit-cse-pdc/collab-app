package edu.manipal.cse.questionbankservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.manipal.cse.questionbankservice.entities.Question;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuestionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID questionId;
    private UUID facultyId;
    private UUID chapterId;
    private String title;
    private String text;
    private Question.QuestionType questionType;
    private List<AnswerResponse> answers;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}