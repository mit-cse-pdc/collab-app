package com.pdc.questionbankservice.services.impl;

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
import lombok.RequiredArgsConstructor;
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
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"chapterList", "chapter", "chapterStats"}, allEntries = true)
    public ChapterResponse createChapter(CreateChapterRequest request) {
        if (checkChapterExists(request.getCourseId(), request.getChapterNo())) {
            throw new IllegalArgumentException("Chapter number already exists for this course.");
        }
        if (checkChapterNameExists(request.getCourseId(), request.getName())) {
            throw new IllegalArgumentException("Chapter name already exists for this course.");
        }

        Chapter chapter = chapterMapper.toEntity(request);
        Chapter savedChapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(savedChapter);
    }

    @Override
    @Cacheable(cacheNames = "chapterList", key = "#courseId")
    public ChapterListResponse getAllChaptersByCourseId(UUID courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByChapterNo(courseId);
        List<ChapterResponse> chapterResponses = chapters.stream().map(chapterMapper::toResponse).collect(Collectors.toList());
        return ChapterListResponse.builder()
                .chapters(chapterResponses)
                .totalChapters(chapterResponses.size())
                .build();
    }

    @Override
    @Cacheable(cacheNames = "chapter", key = "#chapterId")
    public ChapterResponse getChapter(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with ID: " + chapterId));
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"chapterList", "chapter", "chapterStats"}, allEntries = true)
    public ChapterResponse updateChapter(UUID chapterId, UpdateChapterRequest request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with ID: " + chapterId));

        if (request.getName() != null && !request.getName().equalsIgnoreCase(chapter.getName())
                && checkChapterNameExists(chapter.getCourseId(), request.getName())) {
            throw new IllegalArgumentException("Chapter name already exists for this course.");
        }

        chapterMapper.updateEntity(request, chapter);
        Chapter updatedChapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(updatedChapter);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"chapterList", "chapter", "chapterStats"}, allEntries = true)
    public ChapterResponse updateChapterOrder(UpdateChapterOrderRequest request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with ID: " + request.getChapterId()));

        if (checkChapterExists(chapter.getCourseId(), request.getNewChapterNo())) {
            throw new IllegalArgumentException("New chapter number already exists for this course.");
        }

        // Assuming you have a method in the repository to update chapter order or directly updating here
        chapter.setChapterNo(request.getNewChapterNo());
        Chapter updatedChapter = chapterRepository.save(chapter);

        return chapterMapper.toResponse(updatedChapter);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"chapterList", "chapter", "chapterStats"}, allEntries = true)
    public void deleteChapter(UUID chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new ResourceNotFoundException("Chapter not found with ID: " + chapterId);
        }
        chapterRepository.deleteById(chapterId);
    }

    @Override
    public boolean checkChapterExists(UUID courseId, Integer chapterNo) {
        return chapterRepository.existsByCourseIdAndChapterNo(courseId, chapterNo);
    }

    @Override
    public boolean checkChapterNameExists(UUID courseId, String name) {
        return chapterRepository.existsByCourseIdAndNameIgnoreCase(courseId, name);
    }

    @Override
    @Cacheable(cacheNames = "chapterStats", key = "#chapterId")
    public ChapterStatsResponse getChapterStats(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with ID: " + chapterId));

        List<Question> questions = questionRepository.findByChapter_ChapterId(chapterId);
        int totalQuestions = questions.size();

        Map<Question.QuestionType, Long> questionsCountByType = questions.stream()
                .collect(Collectors.groupingBy(Question::getQuestionType, Collectors.counting()));

        Map<Question.QuestionType, Integer> questionsByType = questionsCountByType.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().intValue()
                ));

        return ChapterStatsResponse.builder()
                .chapterId(chapter.getChapterId())
                .name(chapter.getName())
                .totalQuestions(totalQuestions)
                .questionsByType(questionsByType)
                .courseId(chapter.getCourseId())
                .chapterNo(chapter.getChapterNo())
                .build();
    }
}