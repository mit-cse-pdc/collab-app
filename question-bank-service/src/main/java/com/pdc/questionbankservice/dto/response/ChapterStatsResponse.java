package com.pdc.questionbankservice.dto.response;

import com.pdc.questionbankservice.entities.Question;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ChapterStatsResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID chapterId;
    private String name;
    private int totalQuestions;
    private Map<Question.QuestionType, Integer> questionsByType;
    private UUID courseId;
    private Integer chapterNo;
}
