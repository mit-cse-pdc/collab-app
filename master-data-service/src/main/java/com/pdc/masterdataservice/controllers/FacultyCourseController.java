package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.FacultyCourseDto;
import com.pdc.masterdataservice.dto.request.CreateFacultyCourseDto;
import com.pdc.masterdataservice.dto.response.ErrorResponse;
import com.pdc.masterdataservice.services.FacultyCourseService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/faculty-courses")
@RequiredArgsConstructor
@Tag(name = "Faculty Course Management", description = "APIs for managing faculty course assignments")
public class FacultyCourseController {

    private final FacultyCourseService facultyCourseService;

    @Operation(
            summary = "Assign course to faculty",
            description = "Creates a new faculty-course assignment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Course assigned successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FacultyCourseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Course is already assigned to faculty",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<FacultyCourseDto> assignCourse(
            @Valid @RequestBody CreateFacultyCourseDto createFacultyCourseDto
    ) {
        return new ResponseEntity<>(
                facultyCourseService.assignCourse(createFacultyCourseDto),
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Unassign course from faculty",
            description = "Removes a faculty-course assignment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course unassigned successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assignment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{facultyId}/{courseId}")
    public ResponseEntity<Void> unassignCourse(
            @PathVariable UUID facultyId,
            @PathVariable UUID courseId
    ) {
        facultyCourseService.unassignCourse(facultyId, courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get faculty assignments",
            description = "Retrieves all course assignments for a faculty"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assignments retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FacultyCourseDto.class))
                    )
            )
    })
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<FacultyCourseDto>> getFacultyAssignments(
            @PathVariable UUID facultyId
    ) {
        return ResponseEntity.ok(facultyCourseService.getFacultyAssignments(facultyId));
    }

    @Operation(
            summary = "Get course assignments",
            description = "Retrieves all faculty assignments for a course"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assignments retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FacultyCourseDto.class))
                    )
            )
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<FacultyCourseDto>> getCourseAssignments(
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(facultyCourseService.getCourseAssignments(courseId));
    }

    @Operation(
            summary = "Check assignment status",
            description = "Checks if a course is assigned to a faculty"
    )
    @GetMapping("/{facultyId}/{courseId}/check")
    public ResponseEntity<Boolean> isAssigned(
            @PathVariable UUID facultyId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(facultyCourseService.isAssigned(facultyId, courseId));
    }
}