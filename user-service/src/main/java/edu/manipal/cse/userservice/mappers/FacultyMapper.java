package edu.manipal.cse.userservice.mappers;

import edu.manipal.cse.userservice.dto.request.FacultyCreateRequest;
import edu.manipal.cse.userservice.dto.request.FacultyUpdateRequest;
import edu.manipal.cse.userservice.dto.response.FacultyResponse;
import edu.manipal.cse.userservice.entities.Faculty;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class FacultyMapper implements Serializable {
    private final ModelMapper modelMapper;

    public FacultyResponse toResponse(Faculty faculty) {
        return modelMapper.map(faculty, FacultyResponse.class);
    }

    public Faculty toEntity(FacultyCreateRequest request) {
        return modelMapper.map(request, Faculty.class);
    }

    public void updateEntity(FacultyUpdateRequest request, Faculty faculty) {
        modelMapper.map(request, faculty);
    }
}