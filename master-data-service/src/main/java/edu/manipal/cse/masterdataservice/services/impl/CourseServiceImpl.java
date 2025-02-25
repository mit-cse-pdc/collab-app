package edu.manipal.cse.masterdataservice.services.impl;

import edu.manipal.cse.masterdataservice.dto.request.CreateCourseDto;
import edu.manipal.cse.masterdataservice.dto.request.UpdateCourseDto;
import edu.manipal.cse.masterdataservice.dto.response.CourseDto;
import edu.manipal.cse.masterdataservice.entities.Course;
import edu.manipal.cse.masterdataservice.entities.Specialization;
import edu.manipal.cse.masterdataservice.exceptions.DuplicateResourceException;
import edu.manipal.cse.masterdataservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.masterdataservice.mappers.CourseMapper;
import edu.manipal.cse.masterdataservice.repositories.CourseRepository;
import edu.manipal.cse.masterdataservice.repositories.SpecializationRepository;
import edu.manipal.cse.masterdataservice.services.CourseService;
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
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final SpecializationRepository specializationRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "course", key = "#result.courseId")},
            evict = {
                    @CacheEvict(value = "courseList", allEntries = true),
                    @CacheEvict(value = "specializationCourses", key = "#createCourseDto.specializationId")
            }
    )
    public CourseDto createCourse(CreateCourseDto createCourseDto) {
        if (courseRepository.existsByCourseCodeIgnoreCase(createCourseDto.getCourseCode())) {
            throw new DuplicateResourceException(
                    String.format("Course with code '%s' already exists", createCourseDto.getCourseCode())
            );
        }

        Specialization specialization = specializationRepository.findById(createCourseDto.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Specialization not found with ID: %s", createCourseDto.getSpecializationId())
                ));

        Course course = courseMapper.toEntity(createCourseDto, specialization);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    @Override
    @Cacheable(value = "course", key = "#courseId", unless = "#result == null")
    public CourseDto getCourseById(UUID courseId) {
        Course course = findCourseOrThrow(courseId);
        return courseMapper.toDto(course);
    }

    @Override
    @Cacheable(value = "course", key = "#courseCode", unless = "#result == null")
    public CourseDto getCourseByCode(String courseCode) {
        Course course = courseRepository.findByCourseCodeIgnoreCase(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with code: %s", courseCode)
                ));
        return courseMapper.toDto(course);
    }

    @Override
    @Cacheable(value = "courseList", unless = "#result.isEmpty()")
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Cacheable(value = "specializationCourses", key = "#specializationId", unless = "#result.isEmpty()")
    public List<CourseDto> getCoursesBySpecialization(UUID specializationId) {
        List<Course> courses = courseRepository.findBySpecializationSpecializationId(specializationId);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "course", key = "#courseId")},
            evict = {
                    @CacheEvict(value = "courseList", allEntries = true),
                    @CacheEvict(value = "specializationCourses", allEntries = true)
            }
    )
    public CourseDto updateCourse(UUID courseId, UpdateCourseDto updateCourseDto) {
        Course course = findCourseOrThrow(courseId);
        courseMapper.updateEntityFromDto(updateCourseDto, course);
        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toDto(updatedCourse);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "course", key = "#courseId"),
            @CacheEvict(value = "courseList", allEntries = true),
            @CacheEvict(value = "specializationCourses", allEntries = true)
    })
    public void deleteCourse(UUID courseId) {
        Course course = findCourseOrThrow(courseId);
        courseRepository.delete(course);
    }

    @Override
    public Boolean courseExistsById(UUID id) {
        return courseRepository.existsById(id);
    }

    private Course findCourseOrThrow(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with ID: %s", courseId)
                ));
    }
}