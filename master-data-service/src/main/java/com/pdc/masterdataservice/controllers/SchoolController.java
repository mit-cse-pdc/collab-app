package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.SchoolDto;
import com.pdc.masterdataservice.dto.request.CreateSchoolDto;
import com.pdc.masterdataservice.dto.request.UpdateSchoolDto;
import com.pdc.masterdataservice.dto.response.ApiResponse;
import com.pdc.masterdataservice.services.SchoolService;
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
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Tag(name = "School Management", description = "APIs for managing schools")
public class SchoolController {

    private final SchoolService schoolService;

    @Operation(
            summary = "Create a new school",
            description = "Creates a new school with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "School created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 201,
                        "message": "School created successfully",
                        "data": {
                            "schoolId": "123e4567-e89b-12d3-a456-426614174000",
                            "name": "School of Engineering",
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
                    description = "Invalid input provided",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<SchoolDto>> createSchool(@Valid @RequestBody CreateSchoolDto createSchoolDto) {
        SchoolDto school = schoolService.createSchool(createSchoolDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(school, "School created successfully", HttpStatus.CREATED));
    }

    @Operation(
            summary = "Get a school by ID",
            description = "Retrieves school details by its UUID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "School found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "School not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{schoolId}")
    public ResponseEntity<ApiResponse<SchoolDto>> getSchool(@PathVariable UUID schoolId) {
        SchoolDto school = schoolService.getSchoolById(schoolId);
        return ResponseEntity.ok(
                ResponseUtil.success(school, "School fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get all schools",
            description = "Retrieves a list of all schools"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Schools retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SchoolDto>>> getAllSchools() {
        List<SchoolDto> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(
                ResponseUtil.success(schools, "Schools fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Update a school",
            description = "Updates an existing school's details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "School updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "School not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PutMapping("/{schoolId}")
    public ResponseEntity<ApiResponse<SchoolDto>> updateSchool(
            @PathVariable UUID schoolId,
            @Valid @RequestBody UpdateSchoolDto updateSchoolDto) {
        SchoolDto school = schoolService.updateSchool(schoolId, updateSchoolDto);
        return ResponseEntity.ok(
                ResponseUtil.success(school, "School updated successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Delete a school",
            description = "Deletes a school by its UUID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "School deleted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "School not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{schoolId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable UUID schoolId) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.ok(
                ResponseUtil.success(null, "School deleted successfully", HttpStatus.OK)
        );
    }
}