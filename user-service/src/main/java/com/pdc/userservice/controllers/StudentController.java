package com.pdc.userservice.controllers;

import com.pdc.userservice.dto.request.StudentCreateRequest;
import com.pdc.userservice.dto.request.StudentUpdateRequest;
import com.pdc.userservice.dto.response.ErrorResponse;
import com.pdc.userservice.dto.response.StudentResponse;
import com.pdc.userservice.services.StudentService;
import com.pdc.userservice.validators.ValidRegistrationNumber;
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
@RequestMapping("/api/v1/students")
@Tag(name = "Student", description = "Student management APIs")
@Validated
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Create a new student", description = "Creates a new student with the provided information")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student created successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Student already exists with given email or registration number",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        return new ResponseEntity<>(studentService.createStudent(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a student by ID", description = "Returns a student based on the provided ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student found",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(
            @Parameter(description = "Student ID", required = true) @PathVariable UUID id
    ) {
        StudentResponse studentResponse = studentService.getStudentById(id);
        return ResponseEntity.ok(studentResponse);
    }

    @Operation(summary = "Get a student by email", description = "Returns a student based on the provided email")
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentResponse> getStudentByEmail(
            @Parameter(description = "Student email", required = true) @PathVariable @Email(message = "Invalid email format") String email) {
        StudentResponse studentResponse = studentService.getStudentByEmail(email);
        return ResponseEntity.ok(studentResponse);
    }

    @Operation(summary = "Get a student by registration number",
            description = "Returns a student based on the provided registration number")
    @GetMapping("/registration/{registrationNo}")
    public ResponseEntity<StudentResponse> getStudentByRegistrationNo(
            @Parameter(description = "Registration number", required = true) @PathVariable @ValidRegistrationNumber String registrationNo) {
        StudentResponse studentResponse = studentService.getStudentByRegistrationNo(registrationNo);
        return ResponseEntity.ok(studentResponse);
    }

    @Operation(summary = "Get all students", description = "Returns a list of all students")
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @Operation(summary = "Update a student", description = "Updates an existing student's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable UUID id, @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @Operation(summary = "Delete a student", description = "Deletes a student by their ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if email exists",
            description = "Checks if a student exists with the given email")
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email to check", required = true) @PathVariable @Email(message = "Invalid email format") String email) {
        return ResponseEntity.ok(studentService.existsByEmail(email));
    }

    @Operation(summary = "Check if registration number exists",
            description = "Checks if a student exists with the given registration number")
    @GetMapping("/exists/registration/{registrationNo}")
    public ResponseEntity<Boolean> checkRegistrationNoExists(
            @Parameter(description = "Registration number to check", required = true) @PathVariable @ValidRegistrationNumber String registrationNo) {
        return ResponseEntity.ok(studentService.existsByRegistrationNo(registrationNo));
    }
}