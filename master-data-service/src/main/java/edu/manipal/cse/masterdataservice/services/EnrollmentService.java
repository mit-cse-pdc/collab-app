package edu.manipal.cse.masterdataservice.services;

import edu.manipal.cse.masterdataservice.dto.response.EnrollmentDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateEnrollmentDto;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    EnrollmentDto enrollStudent(CreateEnrollmentDto createEnrollmentDto);
    void unenrollStudent(UUID studentId, UUID courseId);
    List<EnrollmentDto> getStudentEnrollments(UUID studentId);
    List<EnrollmentDto> getCourseEnrollments(UUID courseId);
    boolean isEnrolled(UUID studentId, UUID courseId);
}
