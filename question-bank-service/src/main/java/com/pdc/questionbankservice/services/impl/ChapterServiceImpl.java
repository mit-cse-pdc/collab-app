package com.pdc.questionbankservice.services.impl;

import com.pdc.questionbankservice.clients.CourseClient;
import com.pdc.questionbankservice.dto.request.CreateChapterRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterOrderRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterRequest;
import com.pdc.questionbankservice.dto.response.ChapterListResponse;
import com.pdc.questionbankservice.dto.response.ChapterResponse;
import com.pdc.questionbankservice.dto.response.ChapterStatsResponse;
import com.pdc.questionbankservice.entities.Chapter;
import com.pdc.questionbankservice.entities.Question;
import com.pdc.questionbankservice.exceptions.ResourceNotFoundException;
import com.pdc.questionbankservice.mappers.ChapterMapper;
import com.pdc.questionbankservice.repositories.ChapterRepository;
import com.pdc.questionbankservice.repositories.QuestionRepository;
import com.pdc.questionbankservice.services.ChapterService;
import com.pdc.questionbankservice.utils.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final ChapterMapper chapterMapper;
    private final CourseClient courseClient;

    private static final String CHAPTER_CACHE = "chapter";
    private static final String CHAPTER_LIST_CACHE = "chapterList";
    private static final String CHAPTER_STATS_CACHE = "chapterStats";

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CHAPTER_LIST_CACHE, CHAPTER_CACHE, CHAPTER_STATS_CACHE},
            key = "#result.courseId")
    public ChapterResponse createChapter(CreateChapterRequest request) {
        log.debug("Creating new chapter for course ID: {}", request.getCourseId());
        validateCourseExists(request.getCourseId());

        Chapter chapter = chapterMapper.toEntity(request);
        Chapter savedChapter = chapterRepository.save(chapter);

        log.info("Created chapter with ID: {} for course: {}",
                savedChapter.getChapterId(), savedChapter.getCourseId());
        return chapterMapper.toResponse(savedChapter);
    }

    @Override
    @Cacheable(cacheNames = CHAPTER_LIST_CACHE,
            key = "#courseId",
            unless = "#result.chapters.isEmpty()")
    public ChapterListResponse getAllChaptersByCourseId(UUID courseId) {
        log.debug("Fetching all chapters for course ID: {}", courseId);
        validateCourseExists(courseId);

        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByChapterNo(courseId);
        List<ChapterResponse> chapterResponses = chapters.stream()
                .map(chapterMapper::toResponse)
                .collect(Collectors.toList());

        return ChapterListResponse.builder()
                .chapters(chapterResponses)
                .totalChapters(chapterResponses.size())
                .build();
    }

    @Override
    @Cacheable(cacheNames = CHAPTER_CACHE,
            key = "#chapterId",
            unless = "#result == null")
    public ChapterResponse getChapter(UUID chapterId) {
        log.debug("Fetching chapter with ID: {}", chapterId);
        Chapter chapter = findChapterOrThrowException(chapterId);
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CHAPTER_LIST_CACHE, CHAPTER_CACHE, CHAPTER_STATS_CACHE},
            allEntries = true)
    public ChapterResponse updateChapter(UUID chapterId, UpdateChapterRequest request) {
        log.debug("Updating chapter with ID: {}", chapterId);
        Chapter chapter = findChapterOrThrowException(chapterId);

        chapterMapper.updateEntity(request, chapter);
        Chapter updatedChapter = chapterRepository.save(chapter);

        log.info("Updated chapter: {}", chapterId);
        return chapterMapper.toResponse(updatedChapter);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CHAPTER_LIST_CACHE, CHAPTER_CACHE, CHAPTER_STATS_CACHE},
            allEntries = true)
    public ChapterResponse updateChapterOrder(UpdateChapterOrderRequest request) {
        log.debug("Updating order for chapter ID: {} to position: {}",
                request.getChapterId(), request.getNewChapterNo());

        Chapter chapter = findChapterOrThrowException(request.getChapterId());
        chapter.setChapterNo(request.getNewChapterNo());
        Chapter updatedChapter = chapterRepository.save(chapter);

        log.info("Updated chapter order: {}", request.getChapterId());
        return chapterMapper.toResponse(updatedChapter);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CHAPTER_LIST_CACHE, CHAPTER_CACHE, CHAPTER_STATS_CACHE},
            allEntries = true)
    public void deleteChapter(UUID chapterId) {
        log.debug("Deleting chapter with ID: {}", chapterId);
        Chapter chapter = findChapterOrThrowException(chapterId);
        chapterRepository.delete(chapter);
        log.info("Deleted chapter: {}", chapterId);
    }

    @Override
    @Cacheable(cacheNames = CHAPTER_STATS_CACHE,
            key = "#chapterId",
            unless = "#result == null")
    public ChapterStatsResponse getChapterStats(UUID chapterId) {
        log.debug("Fetching stats for chapter ID: {}", chapterId);
        Chapter chapter = findChapterOrThrowException(chapterId);

        List<Question> questions = questionRepository.findByChapter_ChapterId(chapterId);
        Map<Question.QuestionType, Integer> questionsByType = calculateQuestionStats(questions);

        return ChapterStatsResponse.builder()
                .chapterId(chapter.getChapterId())
                .name(chapter.getName())
                .totalQuestions(questions.size())
                .questionsByType(questionsByType)
                .courseId(chapter.getCourseId())
                .chapterNo(chapter.getChapterNo())
                .build();
    }

    private Map<Question.QuestionType, Integer> calculateQuestionStats(List<Question> questions) {
        return questions.stream()
                .collect(Collectors.groupingBy(
                        Question::getQuestionType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    private void validateCourseExists(UUID courseId) {
        if (!courseClient.courseExistsById(courseId)) {
            throw new ResourceNotFoundException(Messages.COURSE_NOT_FOUND + courseId);
        }
    }

    @Override
    public boolean checkChapterExists(UUID courseId, Integer chapterNo) {
        validateCourseExists(courseId);
        return chapterRepository.existsByCourseIdAndChapterNo(courseId, chapterNo);
    }

    @Override
    public boolean checkChapterNameExists(UUID courseId, String name) {
        validateCourseExists(courseId);
        return chapterRepository.existsByCourseIdAndNameIgnoreCase(courseId, name);
    }

    private Chapter findChapterOrThrowException(UUID chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException(Messages.CHAPTER_NOT_FOUND + chapterId));
    }
}