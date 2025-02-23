package com.pdc.masterdataservice.services.impl;

import com.pdc.masterdataservice.dto.response.SpecializationDto;
import com.pdc.masterdataservice.dto.request.CreateSpecializationDto;
import com.pdc.masterdataservice.dto.request.UpdateSpecializationDto;
import com.pdc.masterdataservice.entities.School;
import com.pdc.masterdataservice.entities.Specialization;
import com.pdc.masterdataservice.exceptions.DuplicateResourceException;
import com.pdc.masterdataservice.exceptions.ResourceInUseException;
import com.pdc.masterdataservice.exceptions.ResourceNotFoundException;
import com.pdc.masterdataservice.mappers.SpecializationMapper;
import com.pdc.masterdataservice.repositories.SchoolRepository;
import com.pdc.masterdataservice.repositories.SpecializationRepository;
import com.pdc.masterdataservice.services.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final SchoolRepository schoolRepository;
    private final SpecializationMapper specializationMapper;

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "specialization", key = "#result.specializationId")},
            evict = {
                    @CacheEvict(value = "specializationList", allEntries = true),
                    @CacheEvict(value = "schoolSpecializations", key = "#createSpecializationDto.schoolId")
            }
    )
    public SpecializationDto createSpecialization(CreateSpecializationDto createSpecializationDto) {
        // Check for existing specialization with same name
        if (specializationRepository.existsByNameIgnoreCase(createSpecializationDto.getName())) {
            throw new DuplicateResourceException(
                    String.format("Specialization with name '%s' already exists", createSpecializationDto.getName())
            );
        }

        // Get school
        School school = schoolRepository.findById(createSpecializationDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("School not found with ID: %s", createSpecializationDto.getSchoolId())
                ));

        // Create and save specialization
        Specialization specialization = specializationMapper.toEntity(createSpecializationDto, school);
        Specialization savedSpecialization = specializationRepository.save(specialization);

        return specializationMapper.toDto(savedSpecialization);
    }

    @Override
    @Cacheable(value = "specialization", key = "#specializationId", unless = "#result == null")
    public SpecializationDto getSpecializationById(UUID specializationId) {
        Specialization specialization = findSpecializationOrThrow(specializationId);
        return specializationMapper.toDto(specialization);
    }

    @Override
    @Cacheable(value = "specializationList", unless = "#result.isEmpty()")
    public List<SpecializationDto> getAllSpecializations() {
        List<Specialization> specializations = specializationRepository.findAll();
        return specializationMapper.toDtoList(specializations);
    }

    @Override
    @Cacheable(value = "schoolSpecializations", key = "#schoolId", unless = "#result.isEmpty()")
    public List<SpecializationDto> getSpecializationsBySchool(UUID schoolId) {
        List<Specialization> specializations = specializationRepository.findBySchoolSchoolId(schoolId);
        return specializationMapper.toDtoList(specializations);
    }

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "specialization", key = "#specializationId")},
            evict = {
                    @CacheEvict(value = "specializationList", allEntries = true),
                    @CacheEvict(value = "schoolSpecializations", allEntries = true)
            }
    )
    public SpecializationDto updateSpecialization(UUID specializationId, UpdateSpecializationDto updateSpecializationDto) {
        Specialization specialization = findSpecializationOrThrow(specializationId);

        // Check for name conflicts
        if (!specialization.getName().equalsIgnoreCase(updateSpecializationDto.getName()) &&
                specializationRepository.existsByNameIgnoreCase(updateSpecializationDto.getName())) {
            throw new DuplicateResourceException(
                    String.format("Specialization with name '%s' already exists", updateSpecializationDto.getName())
            );
        }

        specializationMapper.updateEntityFromDto(updateSpecializationDto, specialization);
        Specialization updatedSpecialization = specializationRepository.save(specialization);
        return specializationMapper.toDto(updatedSpecialization);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "specialization", key = "#specializationId"),
            @CacheEvict(value = "specializationList", allEntries = true),
            @CacheEvict(value = "schoolSpecializations", allEntries = true)
    })
    public void deleteSpecialization(UUID specializationId) {
        Specialization specialization = findSpecializationOrThrow(specializationId);

        if (!specialization.getCourses().isEmpty()) {
            throw new ResourceInUseException(
                    String.format("Cannot delete specialization with ID %s as it has associated courses", specializationId)
            );
        }

        specializationRepository.delete(specialization);
    }

    private Specialization findSpecializationOrThrow(UUID specializationId) {
        return specializationRepository.findById(specializationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Specialization not found with ID: %s", specializationId)
                ));
    }
}
