package edu.manipal.cse.questionbankservice.services;

import edu.manipal.cse.questionbankservice.dto.request.CreateChapterRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateChapterOrderRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateChapterRequest;
import edu.manipal.cse.questionbankservice.dto.response.ChapterListResponse;
import edu.manipal.cse.questionbankservice.dto.response.ChapterResponse;
import edu.manipal.cse.questionbankservice.dto.response.ChapterStatsResponse;

import java.util.UUID;

public interface ChapterService {
    ChapterResponse createChapter(CreateChapterRequest request);
    ChapterListResponse getAllChaptersByCourseId(UUID courseId);
    ChapterResponse getChapter(UUID chapterId);
    ChapterResponse updateChapter(UUID chapterId, UpdateChapterRequest request);
    ChapterResponse updateChapterOrder(UpdateChapterOrderRequest request);
    void deleteChapter(UUID chapterId);
    boolean checkChapterExists(UUID courseId, Integer chapterNo);
    boolean checkChapterNameExists(UUID courseId, String name);
    ChapterStatsResponse getChapterStats(UUID chapterId);
}