package com.pdc.questionbankservice.dto.response;

import com.pdc.questionbankservice.entities.Question;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ChapterStatsResponse {
    private UUID chapterId;
    private String name;
    private int totalQuestions;
    private Map<Question.QuestionType, Integer> questionsByType;
    private UUID courseId;
    private Integer chapterNo;
}
