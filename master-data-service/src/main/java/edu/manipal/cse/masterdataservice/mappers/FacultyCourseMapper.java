package edu.manipal.cse.masterdataservice.mappers;

import edu.manipal.cse.masterdataservice.dto.response.FacultyCourseDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateFacultyCourseDto;
import edu.manipal.cse.masterdataservice.entities.Course;
import edu.manipal.cse.masterdataservice.entities.FacultyCourse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacultyCourseMapper {

    private final ModelMapper modelMapper;

    public FacultyCourseDto toDto(FacultyCourse facultyCourse) {
        return modelMapper.map(facultyCourse, FacultyCourseDto.class);
    }

    public FacultyCourse toEntity(CreateFacultyCourseDto dto, Course course) {
        FacultyCourse facultyCourse = modelMapper.map(dto, FacultyCourse.class);
        facultyCourse.setCourse(course);
        return facultyCourse;
    }

    public List<FacultyCourseDto> toDtoList(List<FacultyCourse> facultyCourses) {
        return facultyCourses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}