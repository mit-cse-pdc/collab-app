package com.pdc.questionbankservice.services;

import com.pdc.questionbankservice.dto.request.BulkQuestionRequest;
import com.pdc.questionbankservice.dto.request.CreateQuestionRequest;
import com.pdc.questionbankservice.dto.request.QuestionSearchRequest;
import com.pdc.questionbankservice.dto.request.UpdateQuestionRequest;
import com.pdc.questionbankservice.dto.response.QuestionListResponse;
import com.pdc.questionbankservice.dto.response.QuestionResponse;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    // Create Operations
    QuestionResponse createQuestion(CreateQuestionRequest request);
    List<QuestionResponse> createBulkQuestions(BulkQuestionRequest request);

    // Read Operations
    QuestionListResponse getAllQuestions();
    QuestionResponse getQuestion(UUID questionId);
    QuestionListResponse getQuestionsByChapter(UUID chapterId);
    QuestionListResponse getQuestionsByFaculty(UUID facultyId);
    QuestionListResponse getQuestionsByFacultyAndChapter(UUID facultyId, UUID chapterId);
    QuestionListResponse searchQuestions(QuestionSearchRequest request);

    // Update Operations
    QuestionResponse updateQuestion(UUID questionId, UpdateQuestionRequest request);

    // Delete Operations
    boolean deleteQuestion(UUID questionId);

    // Validation Operations
    boolean checkQuestionOwnershipByFaculty(UUID questionId, UUID facultyId);
    boolean validateQuestionsInChapter(UUID chapterId, List<UUID> questionIds);
}
