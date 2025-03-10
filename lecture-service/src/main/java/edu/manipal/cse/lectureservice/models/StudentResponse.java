package edu.manipal.cse.lectureservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "student_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "response_id")
    private UUID responseId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_question_id", nullable = false)
    private LectureQuestion lectureQuestion;

    @Column(name = "answer_id", nullable = false)
    private UUID answerId;
}