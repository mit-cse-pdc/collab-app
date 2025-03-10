package edu.manipal.cse.lectureservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lecture extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lecture_id")
    private UUID lectureId;

    @Column(name = "faculty_id", nullable = false)
    private UUID facultyId;

    @Column(name = "chapter_id", nullable = false)
    private UUID chapterId;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LectureStatus status = LectureStatus.SCHEDULED;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LectureQuestion> lectureQuestions = new HashSet<>();

    public enum LectureStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}