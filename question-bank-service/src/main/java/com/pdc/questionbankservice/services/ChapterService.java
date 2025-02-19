package com.pdc.questionbankservice.services;

import com.pdc.questionbankservice.dto.request.CreateChapterRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterOrderRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterRequest;
import com.pdc.questionbankservice.dto.response.ChapterListResponse;
import com.pdc.questionbankservice.dto.response.ChapterResponse;
import com.pdc.questionbankservice.dto.response.ChapterStatsResponse;

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