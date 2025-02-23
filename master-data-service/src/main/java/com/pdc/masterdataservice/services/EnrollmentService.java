package com.pdc.masterdataservice.services;

import com.pdc.masterdataservice.dto.response.EnrollmentDto;
import com.pdc.masterdataservice.dto.request.CreateEnrollmentDto;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    EnrollmentDto enrollStudent(CreateEnrollmentDto createEnrollmentDto);
    void unenrollStudent(UUID studentId, UUID courseId);
    List<EnrollmentDto> getStudentEnrollments(UUID studentId);
    List<EnrollmentDto> getCourseEnrollments(UUID courseId);
    boolean isEnrolled(UUID studentId, UUID courseId);
}
