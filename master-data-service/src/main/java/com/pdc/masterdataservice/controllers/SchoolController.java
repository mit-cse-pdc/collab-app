package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.response.ErrorResponse;
import com.pdc.masterdataservice.dto.SchoolDto;
import com.pdc.masterdataservice.dto.request.CreateSchoolDto;
import com.pdc.masterdataservice.dto.request.UpdateSchoolDto;
import com.pdc.masterdataservice.services.SchoolService;
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
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Tag(name = "School Management", description = "APIs for managing schools")
public class SchoolController {

    private final SchoolService schoolService;

    @Operation(
            summary = "Create a new school",
            description = "Creates a new school with the provided details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "School created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SchoolDto.class)
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
                    responseCode = "409",
                    description = "School with the same name already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<SchoolDto> createSchool(
            @Parameter(
                    description = "School creation request body",
                    required = true,
                    schema = @Schema(implementation = CreateSchoolDto.class)
            )
            @Valid @RequestBody CreateSchoolDto createSchoolDto
    ) {
        return new ResponseEntity<>(schoolService.createSchool(createSchoolDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get a school by ID",
            description = "Retrieves school details by its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "School found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SchoolDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "School not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{schoolId}")
    public ResponseEntity<SchoolDto> getSchool(
            @Parameter(
                    description = "UUID of the school to retrieve",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID schoolId
    ) {
        return ResponseEntity.ok(schoolService.getSchoolById(schoolId));
    }

    @Operation(
            summary = "Get all schools",
            description = "Retrieves a list of all schools"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schools retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SchoolDto.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<SchoolDto>> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @Operation(
            summary = "Update a school",
            description = "Updates an existing school's details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "School updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SchoolDto.class)
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
                    description = "School not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Updated name conflicts with an existing school",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{schoolId}")
    public ResponseEntity<SchoolDto> updateSchool(
            @Parameter(
                    description = "UUID of the school to update",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID schoolId,
            @Parameter(
                    description = "School update request body",
                    required = true,
                    schema = @Schema(implementation = UpdateSchoolDto.class)
            )
            @Valid @RequestBody UpdateSchoolDto updateSchoolDto
    ) {
        return ResponseEntity.ok(schoolService.updateSchool(schoolId, updateSchoolDto));
    }

    @Operation(
            summary = "Delete a school",
            description = "Deletes a school by its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "School deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "School not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cannot delete school with associated specializations",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Void> deleteSchool(
            @Parameter(
                    description = "UUID of the school to delete",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID schoolId
    ) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.noContent().build();
    }
}