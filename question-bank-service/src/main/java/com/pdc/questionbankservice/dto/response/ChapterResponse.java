package com.pdc.questionbankservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChapterResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID chapterId;
    private UUID courseId;
    private String name;
    private String description;
    private Integer chapterNo;
    private int questionCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
