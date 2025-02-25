package edu.manipal.cse.userservice.services;

import edu.manipal.cse.userservice.dto.request.FacultyCreateRequest;
import edu.manipal.cse.userservice.dto.request.FacultyUpdateRequest;
import edu.manipal.cse.userservice.dto.response.FacultyResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

public interface FacultyService {
    /**
     * Creates a new faculty member
     * @param request Faculty creation details
     * @return Created faculty response
     * @throws IllegalArgumentException if email already exists
     */
    FacultyResponse createFaculty(FacultyCreateRequest request);

    /**
     * Retrieves a faculty member by their ID
     * @param id Faculty ID
     * @return Optional containing faculty if found
     */
    FacultyResponse getFacultyById(UUID id);

    /**
     * Retrieves a faculty member by their email
     * @param email Faculty email
     * @return Optional containing faculty if found
     */
    FacultyResponse getFacultyByEmail(String email);

    /**
     * Retrieves all faculty members
     * @return List of all faculty members
     */
    List<FacultyResponse> getAllFaculty();

    /**
     * Updates an existing faculty member
     * @param request Faculty update details
     * @return Updated faculty response
     * @throws EntityNotFoundException if faculty not found
     */
    FacultyResponse updateFaculty(UUID id, FacultyUpdateRequest request);

//    AuthFacultyResponse getAuthFacultyByEmail(String email);

    /**
     * Deletes a faculty member by their ID
     * @param id Faculty ID
     */
    void deleteFaculty(UUID id);

    /**
     * Checks if email already exists
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if faculty exists by ID
     * @param id Faculty ID
     * @return true if faculty exists
     */
    boolean existsById(UUID id);
}