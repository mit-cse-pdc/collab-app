package edu.manipal.cse.questionbankservice.services;

import edu.manipal.cse.questionbankservice.dto.request.BulkQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.request.CreateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.request.QuestionSearchRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.response.QuestionListResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionResponse;

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

    List<QuestionResponse> validateAllQuestions(List<UUID> uuids);
}
