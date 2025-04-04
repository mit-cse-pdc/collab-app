package edu.manipal.cse.lectureservicereactive.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("student_responses")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    @Id
    @Column("response_id")
    private UUID responseId;

    @Column("student_id")
    private UUID studentId;

    @Column("lecture_question_id")
    private UUID lectureQuestionId;

    @Column("answer_id")
    private UUID answerId;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
