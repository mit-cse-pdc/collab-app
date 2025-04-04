package edu.manipal.cse.lectureservicereactive.dto.client.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse implements Serializable {
    private UUID chapterId;
    private UUID courseId;
    private String name;
    private String description;
    private Integer chapterNo;
    private Integer questionCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
