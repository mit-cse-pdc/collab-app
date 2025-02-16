package com.pdc.userservice.services.impl;

import com.pdc.userservice.dto.request.StudentCreateRequest;
import com.pdc.userservice.dto.request.StudentUpdateRequest;
import com.pdc.userservice.dto.response.StudentResponse;
import com.pdc.userservice.entities.Student;
import com.pdc.userservice.mappers.StudentMapper;
import com.pdc.userservice.repositories.StudentRepository;
import com.pdc.userservice.services.StudentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "student", allEntries = true),
            @CacheEvict(value = "studentList", allEntries = true)
    })
    public StudentResponse createStudent(StudentCreateRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (existsByRegistrationNo(request.getRegistrationNo())) {
            throw new IllegalArgumentException("Registration number already exists");
        }

        Student student = studentMapper.toEntity(request);
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "#id", unless = "#result == null")
    public StudentResponse getStudentById(UUID id) {

        Student student = studentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + id));

        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "'email:' + #email", unless = "#result == null")
    public StudentResponse getStudentByEmail(String email) {

        Student student = studentRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with email: " + email));

        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "'regNo:' + #registrationNo", unless = "#result == null")
    public StudentResponse getStudentByRegistrationNo(String registrationNo) {

        Student student = studentRepository
                .findByRegistrationNo(registrationNo)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with registration number: " + registrationNo));

        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "studentList")
    public List<StudentResponse> getAllStudents() {
        return studentRepository
                .findAll()
                .stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "student", key = "#id"),
            @CacheEvict(value = "studentList", allEntries = true)
    })
    public StudentResponse updateStudent(UUID id, StudentUpdateRequest request) {
        Student student = studentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        studentMapper.updateEntity(request, student);

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            student.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        student = studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "student", key = "#id"),
            @CacheEvict(value = "studentList", allEntries = true)
    })
    public void deleteStudent(UUID id) {
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "'exists:email:' + #email", unless = "#result == false")
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "'exists:regNo:' + #registrationNo", unless = "#result == false")
    public boolean existsByRegistrationNo(String registrationNo) {
        return studentRepository.existsByRegistrationNo(registrationNo);
    }

    @Override
    public boolean existsById(UUID id) {
        return studentRepository.existsById(id);
    }
}