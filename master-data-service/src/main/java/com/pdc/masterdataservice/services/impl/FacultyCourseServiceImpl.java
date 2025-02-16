package com.pdc.masterdataservice.services.impl;

import com.pdc.masterdataservice.clients.UserClient;
import com.pdc.masterdataservice.dto.FacultyCourseDto;
import com.pdc.masterdataservice.dto.request.CreateFacultyCourseDto;
import com.pdc.masterdataservice.entities.Course;
import com.pdc.masterdataservice.entities.FacultyCourse;
import com.pdc.masterdataservice.exceptions.DuplicateResourceException;
import com.pdc.masterdataservice.exceptions.ResourceNotFoundException;
import com.pdc.masterdataservice.mappers.FacultyCourseMapper;
import com.pdc.masterdataservice.repositories.CourseRepository;
import com.pdc.masterdataservice.repositories.FacultyCourseRepository;
import com.pdc.masterdataservice.services.FacultyCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacultyCourseServiceImpl implements FacultyCourseService {

    private final FacultyCourseRepository facultyCourseRepository;
    private final CourseRepository courseRepository;
    private final FacultyCourseMapper facultyCourseMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "facultyCourse", key = "#result.facultyCourseId")},
            evict = {
                    @CacheEvict(value = "facultyAssignments", key = "#createFacultyCourseDto.facultyId"),
                    @CacheEvict(value = "courseAssignments", key = "#createFacultyCourseDto.courseId")
            }
    )
    public FacultyCourseDto assignCourse(CreateFacultyCourseDto createFacultyCourseDto) {
        // Validate faculty exists
        ResponseEntity<Boolean> facultyExists = userClient.facultyExistsById(createFacultyCourseDto.getFacultyId());
        if (!Boolean.TRUE.equals(facultyExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Faculty not found with ID: %s", createFacultyCourseDto.getFacultyId())
            );
        }

        // Check if already assigned
        if (facultyCourseRepository.existsByFacultyIdAndCourseCourseId(
                createFacultyCourseDto.getFacultyId(),
                createFacultyCourseDto.getCourseId())) {
            throw new DuplicateResourceException("Course is already assigned to this faculty");
        }

        // Get and validate course
        Course course = courseRepository.findById(createFacultyCourseDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with ID: %s", createFacultyCourseDto.getCourseId())
                ));

        // Check if course is active
        if (course.getStatus() != Course.CourseStatus.ACTIVE) {
            throw new IllegalStateException("Cannot assign inactive course");
        }

        FacultyCourse facultyCourse = facultyCourseMapper.toEntity(createFacultyCourseDto, course);
        FacultyCourse savedFacultyCourse = facultyCourseRepository.save(facultyCourse);

        return facultyCourseMapper.toDto(savedFacultyCourse);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "facultyAssignments", key = "#facultyId"),
            @CacheEvict(value = "courseAssignments", key = "#courseId")
    })
    public void unassignCourse(UUID facultyId, UUID courseId) {
        // Validate faculty exists
        ResponseEntity<Boolean> facultyExists = userClient.facultyExistsById(facultyId);
        if (!Boolean.TRUE.equals(facultyExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Faculty not found with ID: %s", facultyId)
            );
        }

        FacultyCourse facultyCourse = facultyCourseRepository
                .findByFacultyIdAndCourseCourseId(facultyId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));

        facultyCourseRepository.delete(facultyCourse);
    }

    @Override
    @Cacheable(value = "facultyAssignments", key = "#facultyId", unless = "#result.isEmpty()")
    public List<FacultyCourseDto> getFacultyAssignments(UUID facultyId) {
        // Validate faculty exists
        ResponseEntity<Boolean> facultyExists = userClient.facultyExistsById(facultyId);
        if (!Boolean.TRUE.equals(facultyExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Faculty not found with ID: %s", facultyId)
            );
        }

        List<FacultyCourse> assignments = facultyCourseRepository.findByFacultyId(facultyId);
        return facultyCourseMapper.toDtoList(assignments);
    }

    @Override
    @Cacheable(value = "courseAssignments", key = "#courseId", unless = "#result.isEmpty()")
    public List<FacultyCourseDto> getCourseAssignments(UUID courseId) {
        // Validate course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    String.format("Course not found with ID: %s", courseId)
            );
        }

        List<FacultyCourse> assignments = facultyCourseRepository.findByCourseCourseId(courseId);
        return facultyCourseMapper.toDtoList(assignments);
    }

    @Override
    public boolean isAssigned(UUID facultyId, UUID courseId) {
        // Validate faculty exists
        ResponseEntity<Boolean> facultyExists = userClient.facultyExistsById(facultyId);
        if (!Boolean.TRUE.equals(facultyExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Faculty not found with ID: %s", facultyId)
            );
        }

        return facultyCourseRepository.existsByFacultyIdAndCourseCourseId(facultyId, courseId);
    }
}