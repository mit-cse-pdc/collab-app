package edu.manipal.cse.lectureservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "lecture_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lecture_question_id")
    private UUID lectureQuestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LectureQuestionStatus status = LectureQuestionStatus.PENDING;

    @OneToMany(mappedBy = "lectureQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentResponse> studentResponses = new ArrayList<>();

    public enum LectureQuestionStatus {
        PENDING, ACTIVE, COMPLETED
    }
}