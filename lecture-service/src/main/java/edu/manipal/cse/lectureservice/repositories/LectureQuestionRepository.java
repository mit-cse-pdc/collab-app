package edu.manipal.cse.lectureservice.repositories;

import edu.manipal.cse.lectureservice.models.LectureQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LectureQuestionRepository extends JpaRepository<LectureQuestion, UUID> {
}