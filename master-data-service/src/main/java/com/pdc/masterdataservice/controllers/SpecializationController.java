package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.SpecializationDto;
import com.pdc.masterdataservice.dto.request.CreateSpecializationDto;
import com.pdc.masterdataservice.dto.request.UpdateSpecializationDto;
import com.pdc.masterdataservice.dto.response.ErrorResponse;
import com.pdc.masterdataservice.services.SpecializationService;
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
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@Tag(name = "Specialization Management", description = "APIs for managing specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    @Operation(
            summary = "Create a new specialization",
            description = "Creates a new specialization with the provided details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Specialization created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SpecializationDto.class)
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
                    description = "Specialization with the same name already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<SpecializationDto> createSpecialization(
            @Parameter(description = "Specialization creation request body", required = true)
            @Valid @RequestBody CreateSpecializationDto createSpecializationDto
    ) {
        return new ResponseEntity<>(specializationService.createSpecialization(createSpecializationDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get specialization by ID",
            description = "Retrieves specialization details by its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specialization found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SpecializationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{specializationId}")
    public ResponseEntity<SpecializationDto> getSpecialization(
            @Parameter(description = "UUID of the specialization to retrieve", required = true)
            @PathVariable UUID specializationId
    ) {
        return ResponseEntity.ok(specializationService.getSpecializationById(specializationId));
    }

    @Operation(
            summary = "Get all specializations",
            description = "Retrieves a list of all specializations"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specializations retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SpecializationDto.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<SpecializationDto>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @Operation(
            summary = "Get specializations by school",
            description = "Retrieves a list of specializations for a specific school"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specializations retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SpecializationDto.class))
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
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<SpecializationDto>> getSpecializationsBySchool(
            @Parameter(description = "UUID of the school", required = true)
            @PathVariable UUID schoolId
    ) {
        return ResponseEntity.ok(specializationService.getSpecializationsBySchool(schoolId));
    }

    @Operation(
            summary = "Update specialization",
            description = "Updates an existing specialization's details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Specialization updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SpecializationDto.class)
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
                    description = "Updated name conflicts with an existing specialization",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{specializationId}")
    public ResponseEntity<SpecializationDto> updateSpecialization(
            @Parameter(description = "UUID of the specialization to update", required = true)
            @PathVariable UUID specializationId,
            @Parameter(description = "Updated specialization details", required = true)
            @Valid @RequestBody UpdateSpecializationDto updateSpecializationDto
    ) {
        return ResponseEntity.ok(specializationService.updateSpecialization(specializationId, updateSpecializationDto));
    }

    @Operation(
            summary = "Delete specialization",
            description = "Deletes a specialization by its UUID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Specialization deleted successfully"
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
                    description = "Cannot delete specialization with associated courses",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{specializationId}")
    public ResponseEntity<Void> deleteSpecialization(
            @Parameter(description = "UUID of the specialization to delete", required = true)
            @PathVariable UUID specializationId
    ) {
        specializationService.deleteSpecialization(specializationId);
        return ResponseEntity.noContent().build();
    }
}
