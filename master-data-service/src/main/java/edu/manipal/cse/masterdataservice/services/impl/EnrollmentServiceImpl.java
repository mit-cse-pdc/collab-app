package edu.manipal.cse.masterdataservice.services.impl;

import edu.manipal.cse.masterdataservice.clients.UserClient;
import edu.manipal.cse.masterdataservice.dto.response.EnrollmentDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateEnrollmentDto;
import edu.manipal.cse.masterdataservice.dto.response.ApiResponse;
import edu.manipal.cse.masterdataservice.entities.Course;
import edu.manipal.cse.masterdataservice.entities.Enrollment;
import edu.manipal.cse.masterdataservice.exceptions.DuplicateResourceException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.masterdataservice.mappers.EnrollmentMapper;
import edu.manipal.cse.masterdataservice.repositories.CourseRepository;
import edu.manipal.cse.masterdataservice.repositories.EnrollmentRepository;
import edu.manipal.cse.masterdataservice.services.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final UserClient userClient;

    private void validateStudentExists(UUID studentId) {
        ResponseEntity<ApiResponse<Boolean>> response = userClient.studentExistsById(studentId);
        ApiResponse<Boolean> apiResponse = response.getBody();

        if (apiResponse == null || apiResponse.getData() == null || !apiResponse.getData()) {
            log.error("Student not found with ID: {}", studentId);
            throw new ResourceNotFoundException(
                    String.format("Student not found with ID: %s", studentId)
            );
        }
    }

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
        validateStudentExists(createEnrollmentDto.getStudentId());

        if (enrollmentRepository.existsByStudentIdAndCourseCourseId(
                createEnrollmentDto.getStudentId(),
                createEnrollmentDto.getCourseId())) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }

        Course course = courseRepository.findById(createEnrollmentDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with ID: %s", createEnrollmentDto.getCourseId())
                ));

        if (course.getStatus() != Course.CourseStatus.ACTIVE) {
            throw new IllegalStateException("Cannot enroll in inactive course");
        }

        Enrollment enrollment = enrollmentMapper.toEntity(createEnrollmentDto, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        log.info("Student {} enrolled in course {}", createEnrollmentDto.getStudentId(), createEnrollmentDto.getCourseId());
        return enrollmentMapper.toDto(savedEnrollment);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "studentEnrollments", key = "#studentId"),
            @CacheEvict(value = "courseEnrollments", key = "#courseId")
    })
    public void unenrollStudent(UUID studentId, UUID courseId) {
        validateStudentExists(studentId);

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        log.info("Student {} unenrolled from course {}", studentId, courseId);
    }

    @Override
    @Cacheable(value = "studentEnrollments", key = "#studentId", unless = "#result.isEmpty()")
    public List<EnrollmentDto> getStudentEnrollments(UUID studentId) {
        validateStudentExists(studentId);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        log.debug("Retrieved {} enrollments for student {}", enrollments.size(), studentId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    @Cacheable(value = "courseEnrollments", key = "#courseId", unless = "#result.isEmpty()")
    public List<EnrollmentDto> getCourseEnrollments(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            log.error("Course not found with ID: {}", courseId);
            throw new ResourceNotFoundException(
                    String.format("Course not found with ID: %s", courseId)
            );
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseCourseId(courseId);
        log.debug("Retrieved {} enrollments for course {}", enrollments.size(), courseId);
        return enrollmentMapper.toDtoList(enrollments);
    }

    @Override
    public boolean isEnrolled(UUID studentId, UUID courseId) {
        validateStudentExists(studentId);
        return enrollmentRepository.existsByStudentIdAndCourseCourseId(studentId, courseId);
    }
}