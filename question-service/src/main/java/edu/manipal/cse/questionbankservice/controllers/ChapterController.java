package edu.manipal.cse.questionbankservice.controllers;

import edu.manipal.cse.questionbankservice.dto.request.CreateChapterRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateChapterOrderRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateChapterRequest;
import edu.manipal.cse.questionbankservice.dto.response.ApiResponse;
import edu.manipal.cse.questionbankservice.dto.response.ChapterListResponse;
import edu.manipal.cse.questionbankservice.dto.response.ChapterResponse;
import edu.manipal.cse.questionbankservice.dto.response.ChapterStatsResponse;
import edu.manipal.cse.questionbankservice.services.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter Controller", description = "Endpoints for managing chapters")
public class ChapterController {
    private final ChapterService chapterService;

    @PostMapping
    @Operation(summary = "Create a new chapter")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(@RequestBody CreateChapterRequest request) {
        ChapterResponse response = chapterService.createChapter(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(response, "Chapter created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all chapters for a course")
    public ResponseEntity<ApiResponse<ChapterListResponse>> getAllChaptersByCourseId(@PathVariable UUID courseId) {
        ChapterListResponse response = chapterService.getAllChaptersByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Chapters retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/{chapterId}")
    @Operation(summary = "Get a chapter by ID")
    public ResponseEntity<ApiResponse<ChapterResponse>> getChapter(@PathVariable UUID chapterId) {
        ChapterResponse response = chapterService.getChapter(chapterId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Chapter retrieved successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{chapterId}")
    @Operation(summary = "Update a chapter")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapter(@PathVariable UUID chapterId, @RequestBody UpdateChapterRequest request) {
        ChapterResponse response = chapterService.updateChapter(chapterId, request);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Chapter updated successfully", HttpStatus.OK.value()));
    }

    @PatchMapping("/order")
    @Operation(summary = "Update chapter order")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapterOrder(@RequestBody UpdateChapterOrderRequest request) {
        ChapterResponse response = chapterService.updateChapterOrder(request);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Chapter order updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{chapterId}")
    @Operation(summary = "Delete a chapter")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(@PathVariable UUID chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.ok(ApiResponse.createSuccess(null, "Chapter deleted successfully", HttpStatus.NO_CONTENT.value()));
    }

    @GetMapping("/{chapterId}/stats")
    @Operation(summary = "Get chapter statistics")
    public ResponseEntity<ApiResponse<ChapterStatsResponse>> getChapterStats(@PathVariable UUID chapterId) {
        ChapterStatsResponse response = chapterService.getChapterStats(chapterId);
        return ResponseEntity.ok(ApiResponse.createSuccess(response, "Chapter statistics retrieved successfully", HttpStatus.OK.value()));
    }
}