package edu.manipal.cse.lectureservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "lectures")
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

    // --- Auditing Fields ---
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    // Enum definition remains the same
    public enum LectureStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}