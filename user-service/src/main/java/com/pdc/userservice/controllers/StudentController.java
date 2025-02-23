package com.pdc.userservice.controllers;

import com.pdc.userservice.dto.request.StudentCreateRequest;
import com.pdc.userservice.dto.request.StudentUpdateRequest;
import com.pdc.userservice.dto.response.ApiResponse;
import com.pdc.userservice.dto.response.StudentResponse;
import com.pdc.userservice.services.StudentService;
import com.pdc.userservice.validators.ValidRegistrationNumber;
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
@RequestMapping("/api/v1/students")
@Tag(name = "Student Management", description = "APIs for managing students")
@SecurityRequirement(name = "bearer-jwt")
@Validated
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(
            summary = "Create a new student",
            description = "Creates a new student with the provided information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Student created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "status": 201,
                        "message": "Student created successfully",
                        "data": {
                            "studentId": "123e4567-e89b-12d3-a456-426614174000",
                            "registrationNo": "202012345",
                            "name": "John Smith",
                            "email": "john.smith@student.example.com"
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
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "status": 400,
                        "message": "Invalid request data",
                        "data": null,
                        "errors": [
                            {
                                "field": "email",
                                "message": "must be a valid email"
                            }
                        ],
                        "timestamp": "2025-02-17T14:25:00Z"
                    }
                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(studentService.createStudent(request), "Student created successfully", HttpStatus.CREATED.value()));
    }

    @Operation(
            summary = "Get student by ID",
            description = "Retrieves student information using their ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @Parameter(description = "Student ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.getStudentById(id),
                "Student fetched successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Get student by email",
            description = "Retrieves student information using their email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByEmail(
            @Parameter(description = "Student email", example = "john.smith@student.example.com", required = true)
            @PathVariable @Email(message = "Invalid email format") String email) {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.getStudentByEmail(email),
                "Student fetched successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Get student by registration number",
            description = "Retrieves student information using their registration number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/registration/{registrationNo}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByRegistrationNo(
            @Parameter(description = "Registration number", example = "202012345", required = true)
            @PathVariable @ValidRegistrationNumber String registrationNo) {
        StudentResponse student = studentService.getStudentByRegistrationNo(registrationNo);
        return ResponseEntity.ok(ApiResponse.createSuccess(student, "Student fetched successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Get all students",
            description = "Retrieves a list of all students"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Students list retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.getAllStudents(),
                "Students list fetched successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Update student",
            description = "Updates an existing student's information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @Parameter(description = "Student ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.updateStudent(id, request),
                "Student updated successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Delete student",
            description = "Deletes a student by their ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student deleted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.createSuccess(null, "Student deleted successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Check email existence",
            description = "Checks if a student exists with the given email"
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
            @Parameter(description = "Email to check", example = "john.smith@student.example.com", required = true)
            @PathVariable @Email(message = "Invalid email format") String email) {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.existsByEmail(email),
                "Email existence checked successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Check registration number existence",
            description = "Checks if a student exists with the given registration number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/exists/registration/{registrationNo}")
    public ResponseEntity<ApiResponse<Boolean>> checkRegistrationNoExists(
            @Parameter(description = "Registration number to check", example = "202012345", required = true)
            @PathVariable @ValidRegistrationNumber String registrationNo) {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.existsByRegistrationNo(registrationNo),
                "Registration number existence checked successfully", HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Check student existence by ID",
            description = "Checks if a student exists with the given ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Check completed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping("/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> checkStudentExists(
            @Parameter(description = "Student ID to check", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.createSuccess(studentService.existsById(id),
                "Student existence checked successfully", HttpStatus.OK.value()));
    }
}