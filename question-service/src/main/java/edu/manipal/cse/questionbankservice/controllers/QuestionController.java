package edu.manipal.cse.questionbankservice.controllers;

import edu.manipal.cse.questionbankservice.dto.request.BulkQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.request.CreateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.request.QuestionSearchRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.response.ApiResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionListResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionResponse;
import edu.manipal.cse.questionbankservice.services.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Question Controller", description = "Endpoints for managing questions")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "Create a new question")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@RequestBody CreateQuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(response, "Question created successfully", HttpStatus.CREATED.value()));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple questions in bulk")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> createBulkQuestions(@RequestBody BulkQuestionRequest request) {
        List<QuestionResponse> responses = questionService.createBulkQuestions(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(responses, "Questions created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get a question by ID")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestion(@PathVariable UUID questionId) {
        QuestionResponse response = questionService.getQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Question retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "Get questions by chapter ID")
    public ResponseEntity<ApiResponse<QuestionListResponse>> getQuestionsByChapter(@PathVariable UUID chapterId) {
        QuestionListResponse response = questionService.getQuestionsByChapter(chapterId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Questions retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/faculty/{facultyId}")
    @Operation(summary = "Get questions by faculty ID")
    public ResponseEntity<ApiResponse<QuestionListResponse>> getQuestionsByFaculty(@PathVariable UUID facultyId) {
        QuestionListResponse response = questionService.getQuestionsByFaculty(facultyId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Questions retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/faculty/{facultyId}/chapter/{chapterId}")
    @Operation(summary = "Get questions by faculty ID and chapter ID")
    public ResponseEntity<ApiResponse<QuestionListResponse>> getQuestionsByFacultyAndChapter(
            @PathVariable UUID facultyId,
            @PathVariable UUID chapterId
    ) {
        QuestionListResponse response = questionService.getQuestionsByFacultyAndChapter(facultyId, chapterId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Questions retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions based on criteria")
    public ResponseEntity<ApiResponse<QuestionListResponse>> searchQuestions(@RequestBody QuestionSearchRequest request) {
        QuestionListResponse response = questionService.searchQuestions(request);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Questions retrieved successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "Update a question")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable UUID questionId,
            @RequestBody UpdateQuestionRequest request
    ) {
        QuestionResponse response = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Question updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete a question")
    public ResponseEntity<ApiResponse<Boolean>> deleteQuestion(@PathVariable UUID questionId) {
        Boolean response = questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Question deleted successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/{questionId}/validate-question-faculty")
    @Operation(summary = "Validate question ownership")
    public ResponseEntity<ApiResponse<Boolean>> validateQuestionOwnership(
            @PathVariable UUID questionId,
            @Parameter(description = "Faculty ID") @RequestParam UUID facultyId
    ) {
        boolean isValid = questionService.checkQuestionOwnershipByFaculty(questionId, facultyId);
        return ResponseEntity.ok(ApiResponse.createSuccess(isValid, "Question ownership validation completed", HttpStatus.OK.value()));
    }

    @GetMapping("/validate-questions-chapter")
    @Operation(summary = "Validate questions in a chapter")
    public ResponseEntity<ApiResponse<Boolean>> validateQuestionsInChapter(
            @Parameter(description = "Chapter ID") @RequestParam UUID chapterId,
            @Parameter(description = "List of question IDs") @RequestBody List<UUID> questionIds
    ) {
        boolean isValid = questionService.validateQuestionsInChapter(chapterId, questionIds);
        return ResponseEntity.ok(ApiResponse.createSuccess(isValid, "Questions validation completed", HttpStatus.OK.value()));
    }

    @GetMapping
    @Operation(summary = "Get all questions")
    public ResponseEntity<ApiResponse<QuestionListResponse>> getAllQuestions() {
        QuestionListResponse response = questionService.getAllQuestions();
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "All questions retrieved successfully", HttpStatus.OK.value()));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate all questions from a list of uuid of question ids")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> validateAllQuestions(@RequestBody List<UUID> uuids) {
        List<QuestionResponse> responses = questionService.validateAllQuestions(uuids);
        return ResponseEntity.ok(ApiResponse.createSuccess(responses, "Questions validated successfully", HttpStatus.OK.value()));
    }
}