package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.CourseDto;
import com.pdc.masterdataservice.dto.request.CreateCourseDto;
import com.pdc.masterdataservice.dto.request.UpdateCourseDto;
import com.pdc.masterdataservice.dto.response.ErrorResponse;
import com.pdc.masterdataservice.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Create a new course",
            description = "Creates a new course with the provided details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Course created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Course with the same code already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(
            @Parameter(description = "Course creation request body", required = true)
            @Valid @RequestBody CreateCourseDto createCourseDto
    ) {
        return new ResponseEntity<>(courseService.createCourse(createCourseDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get course by ID",
            description = "Retrieves course details by its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Course found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourse(
            @Parameter(description = "UUID of the course to retrieve", required = true)
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @Operation(
            summary = "Get course by code",
            description = "Retrieves course details by its code"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Course found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<CourseDto> getCourseByCode(
            @Parameter(description = "Code of the course to retrieve", required = true)
            @PathVariable String courseCode
    ) {
        return ResponseEntity.ok(courseService.getCourseByCode(courseCode));
    }

    @Operation(
            summary = "Get all courses",
            description = "Retrieves a list of all courses"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Courses retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseDto.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(
            summary = "Get courses by specialization",
            description = "Retrieves a list of courses for a specific specialization"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Courses retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseDto.class))
                    )
            )
    })
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<List<CourseDto>> getCoursesBySpecialization(
            @Parameter(description = "UUID of the specialization", required = true)
            @PathVariable UUID specializationId
    ) {
        return ResponseEntity.ok(courseService.getCoursesBySpecialization(specializationId));
    }

    @Operation(
            summary = "Update course",
            description = "Updates an existing course's details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Course updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(
            @Parameter(description = "UUID of the course to update", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "Updated course details", required = true)
            @Valid @RequestBody UpdateCourseDto updateCourseDto
    ) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, updateCourseDto));
    }

    @Operation(
            summary = "Delete course",
            description = "Deletes a course by its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Course deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Course not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "UUID of the course to delete", required = true)
            @PathVariable UUID courseId
    ) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Course exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Boolean.class)
                    )
            )
    )
    @GetMapping("/exists/{id}")
    public Boolean courseExistsById(@Parameter(description = "UUID of course to verify") @PathVariable UUID id) {
        return courseService.courseExistsById(id);
    }
}