package edu.manipal.cse.masterdataservice.services.impl;

import edu.manipal.cse.masterdataservice.clients.UserClient;
import edu.manipal.cse.masterdataservice.dto.response.FacultyCourseDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateFacultyCourseDto;
import edu.manipal.cse.masterdataservice.dto.response.ApiResponse;
import edu.manipal.cse.masterdataservice.entities.Course;
import edu.manipal.cse.masterdataservice.entities.FacultyCourse;
import edu.manipal.cse.masterdataservice.exceptions.DuplicateResourceException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.masterdataservice.mappers.FacultyCourseMapper;
import edu.manipal.cse.masterdataservice.repositories.CourseRepository;
import edu.manipal.cse.masterdataservice.repositories.FacultyCourseRepository;
import edu.manipal.cse.masterdataservice.services.FacultyCourseService;
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
public class FacultyCourseServiceImpl implements FacultyCourseService {

    private final FacultyCourseRepository facultyCourseRepository;
    private final CourseRepository courseRepository;
    private final FacultyCourseMapper facultyCourseMapper;
    private final UserClient userClient;

    private void validateFacultyExists(UUID facultyId) {
        ResponseEntity<ApiResponse<Boolean>> response = userClient.facultyExistsById(facultyId);
        ApiResponse<Boolean> apiResponse = response.getBody();

        if (apiResponse == null || apiResponse.getData() == null || !apiResponse.getData()) {
            log.error("Faculty not found with ID: {}", facultyId);
            throw new ResourceNotFoundException(
                    String.format("Faculty not found with ID: %s", facultyId)
            );
        }
    }

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
        validateFacultyExists(createFacultyCourseDto.getFacultyId());

        if (facultyCourseRepository.existsByFacultyIdAndCourseCourseId(
                createFacultyCourseDto.getFacultyId(),
                createFacultyCourseDto.getCourseId())) {
            throw new DuplicateResourceException("Course is already assigned to this faculty");
        }

        Course course = courseRepository.findById(createFacultyCourseDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with ID: %s", createFacultyCourseDto.getCourseId())
                ));

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
        validateFacultyExists(facultyId);

        FacultyCourse facultyCourse = facultyCourseRepository
                .findByFacultyIdAndCourseCourseId(facultyId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));

        facultyCourseRepository.delete(facultyCourse);
    }

    @Override
    @Cacheable(value = "facultyAssignments", key = "#facultyId", unless = "#result.isEmpty()")
    public List<FacultyCourseDto> getFacultyAssignments(UUID facultyId) {
        validateFacultyExists(facultyId);

        List<FacultyCourse> assignments = facultyCourseRepository.findByFacultyId(facultyId);
        return facultyCourseMapper.toDtoList(assignments);
    }

    @Override
    @Cacheable(value = "courseAssignments", key = "#courseId", unless = "#result.isEmpty()")
    public List<FacultyCourseDto> getCourseAssignments(UUID courseId) {
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
        validateFacultyExists(facultyId);
        return facultyCourseRepository.existsByFacultyIdAndCourseCourseId(facultyId, courseId);
    }
}