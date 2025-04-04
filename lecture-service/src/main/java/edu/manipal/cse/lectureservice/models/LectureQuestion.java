package edu.manipal.cse.lectureservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(mappedBy = "lectureQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudentResponse> studentResponses = new ArrayList<>();

    public enum LectureQuestionStatus {
        PENDING, ACTIVE, COMPLETED
    }

    public void addStudentResponse(StudentResponse response) {
        studentResponses.add(response);
        response.setLectureQuestion(this);
    }

    public void removeStudentResponse(StudentResponse response) {
        studentResponses.remove(response);
        response.setLectureQuestion(null);
    }
}