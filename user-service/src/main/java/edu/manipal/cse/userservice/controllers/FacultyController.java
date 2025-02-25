package edu.manipal.cse.userservice.controllers;

import edu.manipal.cse.userservice.dto.request.FacultyCreateRequest;
import edu.manipal.cse.userservice.dto.request.FacultyUpdateRequest;
import edu.manipal.cse.userservice.dto.response.ApiResponse;
import edu.manipal.cse.userservice.dto.response.FacultyResponse;
import edu.manipal.cse.userservice.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/faculty")
@Tag(name = "Faculty Management", description = "APIs for managing faculty members")
@SecurityRequirement(name = "bearer-jwt")
@Validated
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @Operation(
            summary = "Create a new faculty member",
            description = "Creates a new faculty member with the provided information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Faculty created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "status": 201,
                                        "message": "Faculty created successfully",
                                        "data": {
                                            "facultyId": "123e4567-e89b-12d3-a456-426614174000",
                                            "name": "John Doe",
                                            "email": "john.doe@example.com",
                                            "position": "PROFESSOR"
                                        },
                                        "errors": null,
                                        "timestamp": "2025-02-17T14:25:00Z"
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponse>> createFaculty(@Valid @RequestBody FacultyCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(facultyService.createFaculty(request), "Faculty created successfully", HttpStatus.CREATED.value()));
    }

    @Operation(
            summary = "Get faculty member by ID",
            description = "Retrieves a faculty member's information using their ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Faculty found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "status": 200,
                                        "message": "Faculty fetched successfully",
                                        "data": {
                                            "facultyId": "123e4567-e89b-12d3-a456-426614174000",
                                            "name": "John Doe",
                                            "email": "john.doe@example.com",
                                            "position": "PROFESSOR"
                                        },
                                        "errors": null,
                                        "timestamp": "2025-02-17T14:25:00Z"
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Faculty not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getFacultyById(
            @Parameter(description = "Faculty ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.createSuccess(facultyService.getFacultyById(id), "Faculty fetched successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Get faculty member by email",
            description = "Retrieves a faculty member's information using their email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Faculty found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Faculty not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<FacultyResponse>> getFacultyByEmail(
            @Parameter(description = "Faculty email", example = "john.doe@example.com", required = true)
            @PathVariable @Email(message = "Invalid email format") String email) {
        return ResponseEntity.ok(ApiResponse.createSuccess(facultyService.getFacultyByEmail(email), "Faculty fetched successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Get all faculty members",
            description = "Retrieves a list of all faculty members"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Faculty list retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getAllFaculty() {
        return ResponseEntity.ok(ApiResponse.createSuccess(facultyService.getAllFaculty(), "Faculty list retrieved successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Update faculty member",
            description = "Updates an existing faculty member's information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Faculty updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Faculty not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> updateFaculty(
            @Parameter(description = "Faculty ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody FacultyUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.createSuccess(facultyService.updateFaculty(id, request), "Faculty updated successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Delete faculty member",
            description = "Deletes a faculty member by their ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Faculty deleted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Faculty not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(ApiResponse.createSuccess(null, "Faculty deleted successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Check email existence",
            description = "Checks if a faculty member exists with the given email"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @Parameter(description = "Email to check", example = "john.doe@example.com", required = true)
            @PathVariable @Email(message = "Invalid email format") String email) {
        return ResponseEntity.ok(ApiResponse.createSuccess(facultyService.existsByEmail(email), "Email existence checked successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Check faculty existence by ID",
            description = "Checks if a faculty member exists with the given ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> checkFacultyExists(
            @Parameter(description = "Faculty ID to check", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.createSuccess(facultyService.existsById(id), "Faculty existence checked successfully", HttpStatus.OK.value()));
    }
}