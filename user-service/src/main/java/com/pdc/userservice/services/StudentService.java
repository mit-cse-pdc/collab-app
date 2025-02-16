package com.pdc.userservice.services;

import com.pdc.userservice.dto.request.StudentCreateRequest;
import com.pdc.userservice.dto.request.StudentUpdateRequest;
import com.pdc.userservice.dto.response.StudentResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    /**
     * Creates a new student
     * @param request Student creation details
     * @return Created student response
     * @throws IllegalArgumentException if email or registration number already exists
     */
    StudentResponse createStudent(StudentCreateRequest request);

    /**
     * Retrieves a student by their ID
     * @param id Student ID
     * @return Optional containing student if found
     */
    StudentResponse getStudentById(UUID id);

    /**
     * Retrieves a student by their email
     * @param email Student email
     * @return Optional containing student if found
     */
    StudentResponse getStudentByEmail(String email);

    /**
     * Retrieves a student by their registration number
     * @param registrationNo Student registration number
     * @return Optional containing student if found
     */
    StudentResponse getStudentByRegistrationNo(String registrationNo);

    /**
     * Retrieves all students
     * @return List of all students
     */
    List<StudentResponse> getAllStudents();

    /**
     * Updates an existing student
     * @param request Student update details
     * @return Updated student response
     * @throws EntityNotFoundException if student not found
     */
    StudentResponse updateStudent(UUID id, StudentUpdateRequest request);

    /**
     * Deletes a student by their ID
     * @param id Student ID
     */
    void deleteStudent(UUID id);

    /**
     * Checks if email already exists
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if registration number already exists
     * @param registrationNo Registration number to check
     * @return true if registration number exists
     */
    boolean existsByRegistrationNo(String registrationNo);

    /**
     * Checks if student exists by their ID
     * @param id Student ID
     * @return true if student exists
     */
    boolean existsById(UUID id);
}