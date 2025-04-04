package edu.manipal.cse.questionbankservice.repositories;

import edu.manipal.cse.questionbankservice.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {
    List<Question> findByChapter_ChapterId(UUID chapterId);
    List<Question> findByFacultyId(UUID facultyId);
    List<Question> findByFacultyIdAndChapter_ChapterId(UUID facultyId, UUID chapterId);
    boolean existsByQuestionIdAndFacultyId(UUID questionId, UUID facultyId);
    List<Question> findByChapter_CourseId(UUID courseId);
    List<Question>  findByQuestionIdIn(List<UUID> questionIds);

    // Additional useful methods
    List<Question> findByChapter_ChapterIdAndQuestionType(UUID chapterId, Question.QuestionType questionType);
    long countByChapter_ChapterIdAndQuestionType(UUID chapterId, Question.QuestionType questionType);


    long countQuestionByChapter_ChapterId(UUID chapterChapterId);

    long countByChapter_ChapterIdAndQuestionIdIn(UUID chapterId, List<UUID> questionIds);
}