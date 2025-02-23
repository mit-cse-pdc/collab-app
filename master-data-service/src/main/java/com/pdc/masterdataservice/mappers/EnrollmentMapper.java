package com.pdc.masterdataservice.mappers;

import com.pdc.masterdataservice.dto.response.EnrollmentDto;
import com.pdc.masterdataservice.dto.request.CreateEnrollmentDto;
import com.pdc.masterdataservice.entities.Course;
import com.pdc.masterdataservice.entities.Enrollment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EnrollmentMapper {

    private final ModelMapper modelMapper;

    public EnrollmentDto toDto(Enrollment enrollment) {
        return modelMapper.map(enrollment, EnrollmentDto.class);
    }

    public Enrollment toEntity(CreateEnrollmentDto dto, Course course) {
        Enrollment enrollment = modelMapper.map(dto, Enrollment.class);
        enrollment.setCourse(course);
        return enrollment;
    }

    public List<EnrollmentDto> toDtoList(List<Enrollment> enrollments) {
        return enrollments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}