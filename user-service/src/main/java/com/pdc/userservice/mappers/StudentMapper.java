package com.pdc.userservice.mappers;

import com.pdc.userservice.dto.request.StudentCreateRequest;
import com.pdc.userservice.dto.request.StudentUpdateRequest;
import com.pdc.userservice.dto.response.StudentResponse;
import com.pdc.userservice.entities.Student;
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
