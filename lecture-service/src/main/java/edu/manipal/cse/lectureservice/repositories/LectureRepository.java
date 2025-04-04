package edu.manipal.cse.lectureservice.repositories;

import edu.manipal.cse.lectureservice.models.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LectureRepository extends JpaRepository<Lecture, UUID> {
    @Query("SELECT DISTINCT l FROM Lecture l LEFT JOIN FETCH l.lectureQuestions")
    List<Lecture> findAllWithQuestionsFetched();
}