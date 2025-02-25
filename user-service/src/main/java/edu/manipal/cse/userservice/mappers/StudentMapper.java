package edu.manipal.cse.userservice.mappers;

import edu.manipal.cse.userservice.dto.request.StudentCreateRequest;
import edu.manipal.cse.userservice.dto.request.StudentUpdateRequest;
import edu.manipal.cse.userservice.dto.response.StudentResponse;
import edu.manipal.cse.userservice.entities.Student;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {
    private final ModelMapper modelMapper;

    public StudentResponse toResponse(Student student) {
        return modelMapper.map(student, StudentResponse.class);
    }

    public Student toEntity(StudentCreateRequest request) {
        return modelMapper.map(request, Student.class);
    }

    public void updateEntity(StudentUpdateRequest request, Student student) {
        modelMapper.map(request, student);
    }
}
