package com.pdc.masterdataservice.services.impl;

import com.pdc.masterdataservice.dto.CourseDto;
import com.pdc.masterdataservice.dto.request.CreateCourseDto;
import com.pdc.masterdataservice.dto.request.UpdateCourseDto;
import com.pdc.masterdataservice.entities.Course;
import com.pdc.masterdataservice.entities.Specialization;
import com.pdc.masterdataservice.exceptions.DuplicateResourceException;
import com.pdc.masterdataservice.exceptions.ResourceNotFoundException;
import com.pdc.masterdataservice.mappers.CourseMapper;
import com.pdc.masterdataservice.repositories.CourseRepository;
import com.pdc.masterdataservice.repositories.SpecializationRepository;
import com.pdc.masterdataservice.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

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

    private Course findCourseOrThrow(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Course not found with ID: %s", courseId)
                ));
    }
}