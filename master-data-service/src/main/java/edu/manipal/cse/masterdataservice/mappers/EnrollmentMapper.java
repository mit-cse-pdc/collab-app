package edu.manipal.cse.masterdataservice.mappers;

import edu.manipal.cse.masterdataservice.dto.response.EnrollmentDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateEnrollmentDto;
import edu.manipal.cse.masterdataservice.entities.Course;
import edu.manipal.cse.masterdataservice.entities.Enrollment;
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