package com.pdc.masterdataservice.services.impl;

import com.pdc.masterdataservice.dto.SchoolDto;
import com.pdc.masterdataservice.dto.request.CreateSchoolDto;
import com.pdc.masterdataservice.dto.request.UpdateSchoolDto;
import com.pdc.masterdataservice.entities.School;
import com.pdc.masterdataservice.exceptions.DuplicateResourceException;
import com.pdc.masterdataservice.exceptions.ResourceInUseException;
import com.pdc.masterdataservice.exceptions.ResourceNotFoundException;
import com.pdc.masterdataservice.mappers.SchoolMapper;
import com.pdc.masterdataservice.repositories.SchoolRepository;
import com.pdc.masterdataservice.services.SchoolService;
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
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "school", key = "#result.schoolId")},
            evict = {@CacheEvict(value = "schoolList", allEntries = true)}
    )
    public SchoolDto createSchool(CreateSchoolDto createSchoolDto) {
        // Check for existing school with same name
        if (schoolRepository.existsByNameIgnoreCase(createSchoolDto.getName())) {
            throw new DuplicateResourceException(
                    String.format("School with name '%s' already exists", createSchoolDto.getName())
            );
        }

        School school = schoolMapper.toEntity(createSchoolDto);
        School savedSchool = schoolRepository.save(school);
        return schoolMapper.toDto(savedSchool);
    }

    @Override
    @Cacheable(value = "school", key = "#schoolId", unless = "#result == null")
    public SchoolDto getSchoolById(UUID schoolId) {
        School school = findSchoolOrThrow(schoolId);
        return schoolMapper.toDto(school);
    }

    @Override
    @Cacheable(value = "schoolList", unless = "#result.isEmpty()")
    public List<SchoolDto> getAllSchools() {
        List<School> schools = schoolRepository.findAll();
        return schoolMapper.toDtoList(schools);
    }

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "school", key = "#schoolId")},
            evict = {@CacheEvict(value = "schoolList", allEntries = true)}
    )
    public SchoolDto updateSchool(UUID schoolId, UpdateSchoolDto updateSchoolDto) {
        School school = findSchoolOrThrow(schoolId);

        if (!school.getName().equalsIgnoreCase(updateSchoolDto.getName()) &&
                schoolRepository.existsByNameIgnoreCase(updateSchoolDto.getName())) {
            throw new DuplicateResourceException(
                    String.format("School with name '%s' already exists", updateSchoolDto.getName())
            );
        }

        schoolMapper.updateEntityFromDto(updateSchoolDto, school);
        School updatedSchool = schoolRepository.save(school);
        return schoolMapper.toDto(updatedSchool);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "school", key = "#schoolId"),
            @CacheEvict(value = "schoolList", allEntries = true)
    })
    public void deleteSchool(UUID schoolId) {
        School school = findSchoolOrThrow(schoolId);

        if (!school.getSpecializations().isEmpty()) {
            throw new ResourceInUseException(
                    String.format("Cannot delete school with ID %s as it has associated specializations", schoolId)
            );
        }

        schoolRepository.delete(school);
    }

    private School findSchoolOrThrow(UUID schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("School not found with ID: %s", schoolId)
                ));
    }
}