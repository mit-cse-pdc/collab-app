package edu.manipal.cse.masterdataservice.controllers;

import edu.manipal.cse.masterdataservice.dto.response.FacultyCourseDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateFacultyCourseDto;
import edu.manipal.cse.masterdataservice.dto.response.ApiResponse;
import edu.manipal.cse.masterdataservice.services.FacultyCourseService;
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
@RequestMapping("/api/v1/faculty-courses")
@RequiredArgsConstructor
@Tag(name = "Faculty Course Management", description = "APIs for managing faculty course assignments")
public class FacultyCourseController {

    private final FacultyCourseService facultyCourseService;

    @Operation(
            summary = "Assign course to faculty",
            description = "Creates a new faculty-course assignment"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Course assigned successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 201,
                        "message": "Course assigned successfully",
                        "data": {
                            "facultyId": "123e4567-e89b-12d3-a456-426614174000",
                            "courseId": "123e4567-e89b-12d3-a456-426614174001"
                        },
                        "errors": null,
                        "timestamp": "2025-02-22T10:30:00Z"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<FacultyCourseDto>> assignCourse(
            @Valid @RequestBody CreateFacultyCourseDto createFacultyCourseDto) {
        FacultyCourseDto assignment = facultyCourseService.assignCourse(createFacultyCourseDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(assignment, "Course assigned successfully", HttpStatus.CREATED));
    }

    @Operation(
            summary = "Unassign course from faculty",
            description = "Removes a faculty-course assignment"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Course unassigned successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{facultyId}/{courseId}")
    public ResponseEntity<ApiResponse<Void>> unassignCourse(
            @PathVariable UUID facultyId,
            @PathVariable UUID courseId) {
        facultyCourseService.unassignCourse(facultyId, courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(null, "Course unassigned successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get faculty assignments",
            description = "Retrieves all course assignments for a faculty"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Assignments retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<ApiResponse<List<FacultyCourseDto>>> getFacultyAssignments(
            @PathVariable UUID facultyId) {
        List<FacultyCourseDto> assignments = facultyCourseService.getFacultyAssignments(facultyId);
        return ResponseEntity.ok(
                ResponseUtil.success(assignments, "Faculty assignments fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get course assignments",
            description = "Retrieves all faculty assignments for a course"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Assignments retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<FacultyCourseDto>>> getCourseAssignments(
            @PathVariable UUID courseId) {
        List<FacultyCourseDto> assignments = facultyCourseService.getCourseAssignments(courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(assignments, "Course assignments fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Check assignment status",
            description = "Checks if a course is assigned to a faculty"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{facultyId}/{courseId}/check")
    public ResponseEntity<ApiResponse<Boolean>> isAssigned(
            @PathVariable UUID facultyId,
            @PathVariable UUID courseId) {
        Boolean isAssigned = facultyCourseService.isAssigned(facultyId, courseId);
        return ResponseEntity.ok(
                ResponseUtil.success(isAssigned, "Assignment check completed successfully", HttpStatus.OK)
        );
    }
}