package edu.manipal.cse.masterdataservice.services;

import edu.manipal.cse.masterdataservice.dto.response.SpecializationDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateSpecializationDto;
import edu.manipal.cse.masterdataservice.dto.request.UpdateSpecializationDto;

import java.util.List;
import java.util.UUID;

public interface SpecializationService {
    SpecializationDto createSpecialization(CreateSpecializationDto createSpecializationDto);
    SpecializationDto getSpecializationById(UUID specializationId);
    List<SpecializationDto> getAllSpecializations();
    List<SpecializationDto> getSpecializationsBySchool(UUID schoolId);
    SpecializationDto updateSpecialization(UUID specializationId, UpdateSpecializationDto updateSpecializationDto);
    void deleteSpecialization(UUID specializationId);
}