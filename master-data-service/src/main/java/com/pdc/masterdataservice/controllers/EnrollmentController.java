package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.EnrollmentDto;
import com.pdc.masterdataservice.dto.request.CreateEnrollmentDto;
import com.pdc.masterdataservice.dto.response.ErrorResponse;
import com.pdc.masterdataservice.services.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollment Management", description = "APIs for managing student enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(
            summary = "Enroll student in course",
            description = "Creates a new student enrollment in a course"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Student enrolled successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EnrollmentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Student is already enrolled in this course",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<EnrollmentDto> enrollStudent(
            @Valid @RequestBody CreateEnrollmentDto createEnrollmentDto
    ) {
        return new ResponseEntity<>(
                enrollmentService.enrollStudent(createEnrollmentDto),
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Unroll student from course",
            description = "Removes a student's enrollment from a course"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Student unrolled successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Enrollment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{studentId}/{courseId}")
    public ResponseEntity<Void> unrollStudent(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId
    ) {
        enrollmentService.unenrollStudent(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get student enrollments",
            description = "Retrieves all course enrollments for a student"
    )
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDto>> getStudentEnrollments(
            @PathVariable UUID studentId
    ) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }

    @Operation(
            summary = "Get course enrollments",
            description = "Retrieves all student enrollments for a course"
    )
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDto>> getCourseEnrollments(
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId));
    }

    @Operation(
            summary = "Check enrollment status",
            description = "Checks if a student is enrolled in a course"
    )
    @GetMapping("/{studentId}/{courseId}/check")
    public ResponseEntity<Boolean> isEnrolled(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(enrollmentService.isEnrolled(studentId, courseId));
    }
}