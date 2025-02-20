package com.pdc.masterdataservice.services;

import com.pdc.masterdataservice.dto.CourseDto;
import com.pdc.masterdataservice.dto.request.CreateCourseDto;
import com.pdc.masterdataservice.dto.request.UpdateCourseDto;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseDto createCourse(CreateCourseDto createCourseDto);
    CourseDto getCourseById(UUID courseId);
    CourseDto getCourseByCode(String courseCode);
    List<CourseDto> getAllCourses();
    List<CourseDto> getCoursesBySpecialization(UUID specializationId);
    CourseDto updateCourse(UUID courseId, UpdateCourseDto updateCourseDto);
    void deleteCourse(UUID courseId);

    Boolean courseExistsById(UUID id);
}