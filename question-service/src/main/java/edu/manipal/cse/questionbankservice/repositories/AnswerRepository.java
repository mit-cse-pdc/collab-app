package edu.manipal.cse.questionbankservice.repositories;

import edu.manipal.cse.questionbankservice.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByQuestion_QuestionId(UUID questionId);
    void deleteByQuestion_QuestionId(UUID questionId);

    // Additional useful methods
    Optional<Answer> findByQuestion_QuestionIdAndIsCorrect(UUID questionId, boolean isCorrect); // For TRUE/FALSE questions

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.questionId = :questionId AND a.isCorrect = true")
    long countCorrectAnswersByQuestionId(UUID questionId);
}