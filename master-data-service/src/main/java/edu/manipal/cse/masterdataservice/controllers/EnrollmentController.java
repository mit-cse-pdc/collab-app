package edu.manipal.cse.masterdataservice.controllers;

import edu.manipal.cse.masterdataservice.dto.response.EnrollmentDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateEnrollmentDto;
import edu.manipal.cse.masterdataservice.dto.response.ApiResponse;
import edu.manipal.cse.masterdataservice.services.EnrollmentService;
import edu.manipal.cse.masterdataservice.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Student enrolled successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 201,
                        "message": "Student enrolled successfully",
                        "data": {
                            "studentId": "123e4567-e89b-12d3-a456-426614174000",
                            "courseId": "123e4567-e89b-12d3-a456-426614174001",
                            "enrollmentDate": "2025-02-22T10:30:00Z"
                        },
                        "errors": null,
                        "timestamp": "2025-02-22T10:30:00Z"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentDto>> enrollStudent(
            @Valid @RequestBody CreateEnrollmentDto createEnrollmentDto) {
        EnrollmentDto enrollment = enrollmentService.enrollStudent(createEnrollmentDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(enrollment, "Student enrolled successfully", HttpStatus.CREATED));
    }

    @Operation(
            summary = "Unroll student from course",
            description = "Removes a student's enrollment from a course"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student unrolled successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{studentId}/{courseId}")
    public ResponseEntity<ApiResponse<Void>> unrollStudent(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId) {
        enrollmentService.unenrollStudent(studentId, courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(null, "Student unrolled successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get student enrollments",
            description = "Retrieves all course enrollments for a student"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Enrollments retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> getStudentEnrollments(
            @PathVariable UUID studentId) {
        List<EnrollmentDto> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(
                ResponseUtil.success(enrollments, "Student enrollments fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get course enrollments",
            description = "Retrieves all student enrollments for a course"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Enrollments retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<EnrollmentDto>>> getCourseEnrollments(
            @PathVariable UUID courseId) {
        List<EnrollmentDto> enrollments = enrollmentService.getCourseEnrollments(courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(enrollments, "Course enrollments fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Check enrollment status",
            description = "Checks if a student is enrolled in a course"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{studentId}/{courseId}/check")
    public ResponseEntity<ApiResponse<Boolean>> isEnrolled(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId) {
        Boolean isEnrolled = enrollmentService.isEnrolled(studentId, courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(isEnrolled, "Enrollment check completed successfully", HttpStatus.OK)
        );
    }
}