package edu.manipal.cse.questionbankservice.repositories;

import edu.manipal.cse.questionbankservice.entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    List<Chapter> findByCourseIdOrderByChapterNo(UUID courseId);
    Optional<Chapter> findByCourseIdAndChapterNo(UUID courseId, Integer chapterNo);
    boolean existsByCourseIdAndChapterNo(UUID courseId, Integer chapterNo);

    Optional<Integer> findMaxChapterNoByCourseId(UUID courseId);  // For adding new chapters
    boolean existsByCourseIdAndNameIgnoreCase(UUID courseId, String name); // To prevent duplicate names
}