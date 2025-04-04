package edu.manipal.cse.questionbankservice.controllers;

import edu.manipal.cse.questionbankservice.dto.request.CreateAnswerRequest;
import edu.manipal.cse.questionbankservice.dto.response.AnswerResponse;
import edu.manipal.cse.questionbankservice.dto.response.ApiResponse;
import edu.manipal.cse.questionbankservice.services.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
@Tag(name = "Answer Controller", description = "Endpoints for managing answers")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/{questionId}")
    @Operation(summary = "Create an answer for a question")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Answer created successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid request"),
//            @ApiResponse(responseCode = "404", description = "Question not found")
//    })
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@PathVariable UUID questionId, @RequestBody CreateAnswerRequest request) {
        AnswerResponse response = answerService.createAnswer(request, questionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(edu.manipal.cse.questionbankservice.dto.response.ApiResponse.createSuccess(response, "Answer created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get all answers for a question")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAllAnswersForQuestion(@PathVariable UUID questionId) {
        List<AnswerResponse> responses = answerService.getAllAnswersForQuestion(questionId);
        return ResponseEntity.ok(edu.manipal.cse.questionbankservice.dto.response.ApiResponse.createSuccess(responses, "Answers retrieved successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{answerId}")
    @Operation(summary = "Update an answer")
    public ResponseEntity<ApiResponse<AnswerResponse>> updateAnswer(@PathVariable UUID answerId, @RequestBody CreateAnswerRequest request) {
        AnswerResponse response = answerService.updateAnswer(answerId, request);
        return ResponseEntity.ok(edu.manipal.cse.questionbankservice.dto.response.ApiResponse.createSuccess(response, "Answer updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{answerId}")
    @Operation(summary = "Delete an answer")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(@PathVariable UUID answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.ok(edu.manipal.cse.questionbankservice.dto.response.ApiResponse.createSuccess(null, "Answer deleted successfully", HttpStatus.NO_CONTENT.value()));
    }

    @GetMapping("/answers/{answerId}")
    @Operation(summary = "Get an answer by ID")
    public ResponseEntity<ApiResponse<AnswerResponse>> getAnswer(@PathVariable UUID answerId) {
        AnswerResponse response = answerService.getAnswer(answerId);
        return ResponseEntity.ok(edu.manipal.cse.questionbankservice.dto.response.ApiResponse.createSuccess(response, "Answer retrieved successfully", HttpStatus.OK.value()));
    }
}