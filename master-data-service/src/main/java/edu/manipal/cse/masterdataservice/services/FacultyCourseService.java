package edu.manipal.cse.masterdataservice.services;

import edu.manipal.cse.masterdataservice.dto.response.FacultyCourseDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateFacultyCourseDto;

import java.util.List;
import java.util.UUID;

public interface FacultyCourseService {
    FacultyCourseDto assignCourse(CreateFacultyCourseDto createFacultyCourseDto);
    void unassignCourse(UUID facultyId, UUID courseId);
    List<FacultyCourseDto> getFacultyAssignments(UUID facultyId);
    List<FacultyCourseDto> getCourseAssignments(UUID courseId);
    boolean isAssigned(UUID facultyId, UUID courseId);
}
