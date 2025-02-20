package com.pdc.userservice.controllers;

import com.pdc.userservice.dto.request.FacultyCreateRequest;
import com.pdc.userservice.dto.request.FacultyUpdateRequest;
import com.pdc.userservice.dto.response.AuthFacultyResponse;
import com.pdc.userservice.dto.response.ErrorResponse;
import com.pdc.userservice.dto.response.FacultyResponse;
import com.pdc.userservice.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/faculty")
@Tag(name = "Faculty", description = "Faculty management APIs")
@Validated
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @Operation(summary = "Create a new faculty member", description = "Creates a new faculty member with the provided information")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Faculty created successfully",
                    content = @Content(schema = @Schema(implementation = FacultyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Faculty already exists with given email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<FacultyResponse> createFaculty(@Valid @RequestBody FacultyCreateRequest request) {
        FacultyResponse facultyResponse = facultyService.createFaculty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyResponse);
    }

    @Operation(summary = "Get a faculty member by ID", description = "Returns a faculty member based on the provided ID")
    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFacultyById(
            @Parameter(description = "Faculty ID", required = true) @PathVariable UUID id
    ) {
        FacultyResponse facultyResponse = facultyService.getFacultyById(id);
        return ResponseEntity.ok(facultyResponse);
    }

    @Operation(summary = "Get a faculty member by email", description = "Returns a faculty member based on the provided email")
    @GetMapping("/email/{email}")
    public ResponseEntity<FacultyResponse> getFacultyByEmail(
            @Parameter(description = "Faculty email", required = true) @PathVariable @Email(message = "Invalid email format") String email
    ) {
        FacultyResponse facultyResponse = facultyService.getFacultyByEmail(email);
        return ResponseEntity.ok(facultyResponse);
    }

    @Operation(summary = "Get all faculty members", description = "Returns a list of all faculty members")
    @GetMapping
    public ResponseEntity<List<FacultyResponse>> getAllFaculty() {
        List<FacultyResponse> facultyResponses = facultyService.getAllFaculty();
        return ResponseEntity.ok(facultyResponses);
    }

    @Operation(summary = "Update a faculty member", description = "Updates an existing faculty member's information")
    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponse> updateFaculty(
            @Parameter(description = "Faculty ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody FacultyUpdateRequest request
    ) {
        FacultyResponse facultyResponse = facultyService.updateFaculty(id, request);
        return ResponseEntity.ok(facultyResponse);
    }

    @Operation(summary = "Delete a faculty member", description = "Deletes a faculty member by their ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if email exists", description = "Checks if a faculty member exists with the given email")
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email to check", required = true) @PathVariable @Email(message = "Invalid email format") String email
    ) {
        Boolean exists = facultyService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Check if faculty exists", description = "Checks if a faculty member exists with the given ID")
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkFacultyExists(
            @Parameter(description = "Faculty ID to check", required = true) @PathVariable UUID id
    ) {
        Boolean exists = facultyService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/auth-faculty/{email}")
    public ResponseEntity<AuthFacultyResponse> getAuthFacultyByEmail(@PathVariable String email){
        AuthFacultyResponse response = facultyService.getAuthFacultyByEmail(email);
        return ResponseEntity.ok(response);
    }
}