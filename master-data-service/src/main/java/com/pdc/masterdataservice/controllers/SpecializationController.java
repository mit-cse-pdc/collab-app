package com.pdc.masterdataservice.controllers;

import com.pdc.masterdataservice.dto.SpecializationDto;
import com.pdc.masterdataservice.dto.request.CreateSpecializationDto;
import com.pdc.masterdataservice.dto.request.UpdateSpecializationDto;
import com.pdc.masterdataservice.dto.response.ApiResponse;
import com.pdc.masterdataservice.services.SpecializationService;
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
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@Tag(name = "Specialization Management", description = "APIs for managing specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    @Operation(
            summary = "Create a new specialization",
            description = "Creates a new specialization with the provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Specialization created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 201,
                        "message": "Specialization created successfully",
                        "data": {
                            "specializationId": "123e4567-e89b-12d3-a456-426614174000",
                            "name": "Computer Science",
                            "schoolId": "123e4567-e89b-12d3-a456-426614174001",
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
    public ResponseEntity<ApiResponse<SpecializationDto>> createSpecialization(
            @Valid @RequestBody CreateSpecializationDto createSpecializationDto) {
        SpecializationDto specialization = specializationService.createSpecialization(createSpecializationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(specialization, "Specialization created successfully", HttpStatus.CREATED));
    }

    @Operation(
            summary = "Get specialization by ID",
            description = "Retrieves specialization details by its UUID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Specialization found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{specializationId}")
    public ResponseEntity<ApiResponse<SpecializationDto>> getSpecialization(@PathVariable UUID specializationId) {
        SpecializationDto specialization = specializationService.getSpecializationById(specializationId);
        return ResponseEntity.ok(
                ResponseUtil.success(specialization, "Specialization fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get all specializations",
            description = "Retrieves a list of all specializations"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Specializations retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecializationDto>>> getAllSpecializations() {
        List<SpecializationDto> specializations = specializationService.getAllSpecializations();
        return ResponseEntity.ok(
                ResponseUtil.success(specializations, "Specializations fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Get specializations by school",
            description = "Retrieves a list of specializations for a specific school"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Specializations retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "School not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse<List<SpecializationDto>>> getSpecializationsBySchool(@PathVariable UUID schoolId) {
        List<SpecializationDto> specializations = specializationService.getSpecializationsBySchool(schoolId);
        return ResponseEntity.ok(
                ResponseUtil.success(specializations, "School specializations fetched successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Update specialization",
            description = "Updates an existing specialization's details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Specialization updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PutMapping("/{specializationId}")
    public ResponseEntity<ApiResponse<SpecializationDto>> updateSpecialization(
            @PathVariable UUID specializationId,
            @Valid @RequestBody UpdateSpecializationDto updateSpecializationDto) {
        SpecializationDto specialization = specializationService.updateSpecialization(specializationId, updateSpecializationDto);
        return ResponseEntity.ok(
                ResponseUtil.success(specialization, "Specialization updated successfully", HttpStatus.OK)
        );
    }

    @Operation(
            summary = "Delete specialization",
            description = "Deletes a specialization by its UUID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Specialization deleted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Specialization not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{specializationId}")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialization(@PathVariable UUID specializationId) {
        specializationService.deleteSpecialization(specializationId);
        return ResponseEntity.ok(
                ResponseUtil.success(null, "Specialization deleted successfully", HttpStatus.OK)
        );
    }
}