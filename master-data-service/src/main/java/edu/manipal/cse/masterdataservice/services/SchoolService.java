package edu.manipal.cse.masterdataservice.services;

import edu.manipal.cse.masterdataservice.dto.response.SchoolDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateSchoolDto;
import edu.manipal.cse.masterdataservice.dto.request.UpdateSchoolDto;
import edu.manipal.cse.masterdataservice.exceptions.DuplicateResourceException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceInUseException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing School entities.
 */
public interface SchoolService {
    /**
     * Creates a new school.
     *
     * @param createSchoolDto the DTO containing school creation data
     * @return the created school DTO
     * @throws DuplicateResourceException if a school with the same name already exists
     */
    SchoolDto createSchool(CreateSchoolDto createSchoolDto);

    /**
     * Retrieves a school by its ID.
     *
     * @param schoolId the UUID of the school
     * @return the school DTO
     * @throws ResourceNotFoundException if the school is not found
     */
    SchoolDto getSchoolById(UUID schoolId);

    /**
     * Retrieves all schools with pagination.
     * @return a page of school DTOs
     */
    List<SchoolDto> getAllSchools();

    /**
     * Updates a school by its ID.
     *
     * @param schoolId the UUID of the school to update
     * @param updateSchoolDto the DTO containing update data
     * @return the updated school DTO
     * @throws ResourceNotFoundException if the school is not found
     * @throws DuplicateResourceException if the new name conflicts with an existing school
     */
    SchoolDto updateSchool(UUID schoolId, UpdateSchoolDto updateSchoolDto);

    /**
     * Deletes a school by its ID.
     *
     * @param schoolId the UUID of the school to delete
     * @throws ResourceNotFoundException if the school is not found
     * @throws ResourceInUseException if the school has associated specializations
     */
    void deleteSchool(UUID schoolId);
}