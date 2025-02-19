package com.pdc.questionbankservice.controllers;

import com.pdc.questionbankservice.dto.request.CreateChapterRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterOrderRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterRequest;
import com.pdc.questionbankservice.dto.response.ChapterListResponse;
import com.pdc.questionbankservice.dto.response.ChapterResponse;
import com.pdc.questionbankservice.dto.response.ChapterStatsResponse;
import com.pdc.questionbankservice.services.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chapter created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ChapterResponse> createChapter(@RequestBody CreateChapterRequest request) {
        ChapterResponse response = chapterService.createChapter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all chapters for a course")
    @ApiResponse(responseCode = "200", description = "Chapters retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterListResponse.class)))
    public ResponseEntity<ChapterListResponse> getAllChaptersByCourseId(@PathVariable UUID courseId) {
        ChapterListResponse response = chapterService.getAllChaptersByCourseId(courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chapterId}")
    @Operation(summary = "Get a chapter by ID")
    @ApiResponse(responseCode = "200", description = "Chapter retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class)))
    public ResponseEntity<ChapterResponse> getChapter(@PathVariable UUID chapterId) {
        ChapterResponse response = chapterService.getChapter(chapterId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chapterId}")
    @Operation(summary = "Update a chapter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chapter updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    public ResponseEntity<ChapterResponse> updateChapter(@PathVariable UUID chapterId, @RequestBody UpdateChapterRequest request) {
        ChapterResponse response = chapterService.updateChapter(chapterId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/order")
    @Operation(summary = "Update chapter order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chapter order updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    public ResponseEntity<ChapterResponse> updateChapterOrder(@RequestBody UpdateChapterOrderRequest request) {
        ChapterResponse response = chapterService.updateChapterOrder(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chapterId}")
    @Operation(summary = "Delete a chapter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chapter deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    public ResponseEntity<Void> deleteChapter(@PathVariable UUID chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chapterId}/stats")
    @Operation(summary = "Get chapter statistics")
    @ApiResponse(responseCode = "200", description = "Chapter statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterStatsResponse.class)))
    public ResponseEntity<ChapterStatsResponse> getChapterStats(@PathVariable UUID chapterId) {
        ChapterStatsResponse response = chapterService.getChapterStats(chapterId);
        return ResponseEntity.ok(response);
    }
}