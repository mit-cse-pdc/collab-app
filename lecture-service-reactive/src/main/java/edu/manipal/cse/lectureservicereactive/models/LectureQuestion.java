package edu.manipal.cse.lectureservicereactive.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("lecture_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureQuestion {
    @Id
    @Column("lecture_question_id")
    private UUID lectureQuestionId;

    @Column("lecture_id")
    private UUID lectureId;

    @Column("question_id")
    private UUID questionId;

    @Column("status")
    private LectureQuestionStatus status = LectureQuestionStatus.PENDING;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public enum LectureQuestionStatus {
        PENDING, ACTIVE, COMPLETED
    }
}