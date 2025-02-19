package com.pdc.questionbankservice.controllers;

import com.pdc.questionbankservice.dto.request.BulkQuestionRequest;
import com.pdc.questionbankservice.dto.request.CreateQuestionRequest;
import com.pdc.questionbankservice.dto.request.QuestionSearchRequest;
import com.pdc.questionbankservice.dto.request.UpdateQuestionRequest;
import com.pdc.questionbankservice.dto.response.QuestionListResponse;
import com.pdc.questionbankservice.dto.response.QuestionResponse;
import com.pdc.questionbankservice.services.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody CreateQuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple questions in bulk")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Questions created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    public ResponseEntity<List<QuestionResponse>> createBulkQuestions(@RequestBody BulkQuestionRequest request) {
        List<QuestionResponse> responses = questionService.createBulkQuestions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get a question by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable UUID questionId) {
        QuestionResponse response = questionService.getQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "Get questions by chapter ID")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<QuestionListResponse> getQuestionsByChapter(@PathVariable UUID chapterId) {
        QuestionListResponse response = questionService.getQuestionsByChapter(chapterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/faculty/{facultyId}")
    @Operation(summary = "Get questions by faculty ID")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<QuestionListResponse> getQuestionsByFaculty(@PathVariable UUID facultyId) {
        QuestionListResponse response = questionService.getQuestionsByFaculty(facultyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/faculty/{facultyId}/chapter/{chapterId}")
    @Operation(summary = "Get questions by faculty ID and chapter ID")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<QuestionListResponse> getQuestionsByFacultyAndChapter(
            @PathVariable UUID facultyId,
            @PathVariable UUID chapterId
    ) {
        QuestionListResponse response = questionService.getQuestionsByFacultyAndChapter(facultyId, chapterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions based on criteria")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<QuestionListResponse> searchQuestions(@RequestBody QuestionSearchRequest request) {
        QuestionListResponse response = questionService.searchQuestions(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "Update a question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID questionId,
            @RequestBody UpdateQuestionRequest request
    ) {
        QuestionResponse response = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete a question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<Boolean> deleteQuestion(@PathVariable UUID questionId) {
        Boolean response = questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionId}/validate-question-faculty")
    @Operation(summary = "Validate question ownership")
    @ApiResponse(responseCode = "200", description = "Ownership validation result")
    public ResponseEntity<Boolean> validateQuestionOwnership(
            @PathVariable UUID questionId,
            @Parameter(description = "Faculty ID") @RequestParam UUID facultyId
    ) {
        boolean isValid = questionService.checkQuestionOwnershipByFaculty(questionId, facultyId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validate-questions-chapter")
    @Operation(summary = "Validate questions in a chapter")
    @ApiResponse(responseCode = "200", description = "Questions validation result")
    public ResponseEntity<Boolean> validateQuestionsInChapter(
            @Parameter(description = "Chapter ID") @RequestParam UUID chapterId,
            @Parameter(description = "List of question IDs") @RequestBody List<UUID> questionIds
    ) {
        boolean isValid = questionService.validateQuestionsInChapter(chapterId, questionIds);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping
    @Operation(summary = "Get all questions")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<QuestionListResponse> getAllQuestions() {
        QuestionListResponse response = questionService.getAllQuestions();
        return ResponseEntity.ok(response);
    }
}