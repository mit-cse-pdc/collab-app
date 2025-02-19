package com.pdc.questionbankservice.dto.response;

import com.pdc.questionbankservice.entities.Question;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuestionResponse {
    private UUID questionId;
    private UUID facultyId;
    private UUID chapterId;
    private String title;
    private String text;
    private Question.QuestionType questionType;
    private List<AnswerResponse> answers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}