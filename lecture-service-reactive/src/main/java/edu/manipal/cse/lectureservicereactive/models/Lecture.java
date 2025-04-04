package edu.manipal.cse.lectureservicereactive.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Lecture {

    @Id
    @Column("lecture_id")
    private UUID lectureId;

    @Column("faculty_id")
    private UUID facultyId;

    @Column("chapter_id")
    private UUID chapterId;

    @Column("title")
    private String title;

    @Column("status")
    private LectureStatus status = LectureStatus.SCHEDULED;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public enum LectureStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}