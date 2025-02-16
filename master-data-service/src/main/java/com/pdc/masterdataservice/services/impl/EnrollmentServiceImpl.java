package com.pdc.masterdataservice.services.impl;

import com.pdc.masterdataservice.clients.UserClient;
import com.pdc.masterdataservice.dto.EnrollmentDto;
import com.pdc.masterdataservice.dto.request.CreateEnrollmentDto;
import com.pdc.masterdataservice.entities.Course;
import com.pdc.masterdataservice.entities.Enrollment;
import com.pdc.masterdataservice.exceptions.DuplicateResourceException;
import com.pdc.masterdataservice.exceptions.ResourceNotFoundException;
import com.pdc.masterdataservice.mappers.EnrollmentMapper;
import com.pdc.masterdataservice.repositories.CourseRepository;
import com.pdc.masterdataservice.repositories.EnrollmentRepository;
import com.pdc.masterdataservice.services.EnrollmentService;
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
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "enrollment", key = "#result.enrollmentId")},
            evict = {
                    @CacheEvict(value = "studentEnrollments", key = "#createEnrollmentDto.studentId"),
                    @CacheEvict(value = "courseEnrollments", key = "#createEnrollmentDto.courseId")
            }
    )
    public EnrollmentDto enrollStudent(CreateEnrollmentDto createEnrollmentDto) {
        // Validate student exists
        ResponseEntity<Boolean> studentExists = userClient.studentExistsById(createEnrollmentDto.getStudentId());
        if (!Boolean.TRUE.equals(studentExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Student not found with ID: %s", createEnrollmentDto.getStudentId())
            );
        }

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseCourseId(
                createEnrollmentDto.getStudentId(),
                createEnrollmentDto.getCourseId())) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }

        // Get and validate course
        Course course = courseRepository.findById(createEnrollmentDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with ID: %s", createEnrollmentDto.getCourseId())
                ));

        // Check if course is active
        if (course.getStatus() != Course.CourseStatus.ACTIVE) {
            throw new IllegalStateException("Cannot enroll in inactive course");
        }

        Enrollment enrollment = enrollmentMapper.toEntity(createEnrollmentDto, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.toDto(savedEnrollment);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "studentEnrollments", key = "#studentId"),
            @CacheEvict(value = "courseEnrollments", key = "#courseId")
    })
    public void unenrollStudent(UUID studentId, UUID courseId) {
        // Validate student exists
        ResponseEntity<Boolean> studentExists = userClient.studentExistsById(studentId);
        if (!Boolean.TRUE.equals(studentExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Student not found with ID: %s", studentId)
            );
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Cacheable(value = "studentEnrollments", key = "#studentId", unless = "#result.isEmpty()")
    public List<EnrollmentDto> getStudentEnrollments(UUID studentId) {
        // Validate student exists
        ResponseEntity<Boolean> studentExists = userClient.studentExistsById(studentId);
        if (!Boolean.TRUE.equals(studentExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Student not found with ID: %s", studentId)
            );
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Cacheable(value = "courseEnrollments", key = "#courseId", unless = "#result.isEmpty()")
    public List<EnrollmentDto> getCourseEnrollments(UUID courseId) {
        // Validate course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    String.format("Course not found with ID: %s", courseId)
            );
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseCourseId(courseId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    public boolean isEnrolled(UUID studentId, UUID courseId) {
        // Validate student exists
        ResponseEntity<Boolean> studentExists = userClient.studentExistsById(studentId);
        if (!Boolean.TRUE.equals(studentExists.getBody())) {
            throw new ResourceNotFoundException(
                    String.format("Student not found with ID: %s", studentId)
            );
        }

        return enrollmentRepository.existsByStudentIdAndCourseCourseId(studentId, courseId);
    }
}
