package com.pdc.userservice.services.impl;

import com.pdc.userservice.dto.request.FacultyCreateRequest;
import com.pdc.userservice.dto.request.FacultyUpdateRequest;
import com.pdc.userservice.dto.response.AuthFacultyResponse;
import com.pdc.userservice.dto.response.FacultyResponse;
import com.pdc.userservice.entities.Faculty;
import com.pdc.userservice.mappers.FacultyMapper;
import com.pdc.userservice.repositories.FacultyRepository;
import com.pdc.userservice.services.FacultyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "faculty", allEntries = true),
            @CacheEvict(value = "facultyList", allEntries = true)
    })
    public FacultyResponse createFaculty(FacultyCreateRequest request) {
        if (facultyRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Faculty faculty = facultyMapper.toEntity(request);
        faculty.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        faculty = facultyRepository.save(faculty);
        return facultyMapper.toResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "faculty", key = "#id", unless = "#result == null")
    public FacultyResponse getFacultyById(UUID id) {

        Faculty faculty = facultyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with ID: " + id));

        return facultyMapper.toResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "faculty", key = "'email:' + #email", unless = "#result == null")
    public FacultyResponse getFacultyByEmail(String email) {
        log.info("Finding faculty by email: {}", email);

        Faculty faculty = facultyRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with email: " + email));

        log.info("Faculty found: {}", faculty);
        return facultyMapper.toResponse(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "facultyList")
    public List<FacultyResponse> getAllFaculty() {
        return facultyRepository
                .findAll()
                .stream()
                .map(facultyMapper::toResponse)
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "faculty", key = "#id"),
            @CacheEvict(value = "facultyList", allEntries = true)
    })
    public FacultyResponse updateFaculty(UUID id, FacultyUpdateRequest request) {
        Faculty faculty = facultyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found"));

        facultyMapper.updateEntity(request, faculty);

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            faculty.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        }

        faculty = facultyRepository.save(faculty);
        return facultyMapper.toResponse(faculty);
    }

    @Override
    public AuthFacultyResponse getAuthFacultyByEmail(String email) {
        Faculty faculty = facultyRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with email: " + email));

        log.info("Auth Faculty found: {}", faculty);

        return facultyMapper.toAuthResponse(faculty);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "faculty", key = "#id"),
            @CacheEvict(value = "facultyList", allEntries = true)
    })
    public void deleteFaculty(UUID id) {
        Faculty faculty = facultyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found"));

        // Evict email-based cache before deletion
        facultyRepository.delete(faculty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "faculty", key = "'exists:' + #email", unless = "#result == false")
    public boolean existsByEmail(String email) {
        return facultyRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(UUID id) {
        return facultyRepository.existsById(id);
    }
}