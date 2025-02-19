package com.pdc.questionbankservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChapterResponse {
    private UUID chapterId;
    private UUID courseId;
    private String name;
    private String description;
    private Integer chapterNo;
    private int questionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
