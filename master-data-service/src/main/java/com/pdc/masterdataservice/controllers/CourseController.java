package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.CourseDto;
import com.pdc.masterdataservice.dto.request.CreateCourseDto;
import com.pdc.masterdataservice.dto.request.UpdateCourseDto;
import com.pdc.masterdataservice.dto.response.ApiResponse;
import com.pdc.masterdataservice.services.CourseService;
import com.pdc.masterdataservice.utils.ResponseUtil;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Create a new course",
            description = "Creates a new course with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Course created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 201,
                        "message": "Course created successfully",
                        "data": {
                            "courseId": "123e4567-e89b-12d3-a456-426614174000",
                            "courseCode": "CS101",
                            "name": "Introduction to Programming",
                            "description": "Basic programming concepts",
                            "credits": 3,
                            "semester": 1,
                            "academicYear": 2025,
                            "specializationId": "123e4567-e89b-12d3-a456-426614174001",
                            "status": "ACTIVE",
                            "createdAt": "2025-02-22 10:30:00",
                            "updatedAt": "2025-02-22 10:30:00"
                        },
                        "errors": null,
                        "timestamp": "2025-02-22T10:30:00Z"
                    }
                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(@Valid @RequestBody CreateCourseDto createCourseDto) {
        CourseDto course = courseService.createCourse(createCourseDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(course, "Course created successfully", HttpStatus.CREATED));
    }

    @Operation(
            summary = "Get course by ID",
            description = "Retrieves course details by its UUID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Course found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDto>> getCourse(@PathVariable UUID courseId) {
        CourseDto course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(course, "Course fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get course by code",
            description = "Retrieves course details by its code"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Course found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<ApiResponse<CourseDto>> getCourseByCode(@PathVariable String courseCode) {
        CourseDto course = courseService.getCourseByCode(courseCode);
        return ResponseEntity.ok(
                ResponseUtil.success(course, "Course fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get all courses",
            description = "Retrieves a list of all courses"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Courses retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(
                ResponseUtil.success(courses, "Courses fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get courses by specialization",
            description = "Retrieves a list of courses for a specific specialization"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Courses retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getCoursesBySpecialization(@PathVariable UUID specializationId) {
        List<CourseDto> courses = courseService.getCoursesBySpecialization(specializationId);
        return ResponseEntity.ok(
                ResponseUtil.success(courses, "Specialization courses fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Update course",
            description = "Updates an existing course's details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Course updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseDto>> updateCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody UpdateCourseDto updateCourseDto) {
        CourseDto course = courseService.updateCourse(courseId, updateCourseDto);
        return ResponseEntity.ok(
                ResponseUtil.success(course, "Course updated successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Delete course",
            description = "Deletes a course by its UUID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Course deleted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable UUID courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(null, "Course deleted successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Check if course exists",
            description = "Checks if a course exists by its ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> courseExistsById(@PathVariable UUID id) {
        Boolean exists = courseService.courseExistsById(id);
        return ResponseEntity.ok(
                ResponseUtil.success(exists, "Course existence checked successfully", HttpStatus.OK)
        );
    }
}