package edu.manipal.cse.masterdataservice.mappers;

import edu.manipal.cse.masterdataservice.dto.response.SchoolDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateSchoolDto;
import edu.manipal.cse.masterdataservice.dto.request.UpdateSchoolDto;
import edu.manipal.cse.masterdataservice.entities.School;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SchoolMapper {

    private final ModelMapper modelMapper;

    /**
     * Converts School entity to SchoolDto
     */
    public SchoolDto toDto(School school) {
        return modelMapper.map(school, SchoolDto.class);
    }

    /**
     * Converts CreateSchoolDto to School entity
     */
    public School toEntity(CreateSchoolDto createSchoolDto) {
        return modelMapper.map(createSchoolDto, School.class);
    }

    /**
     * Updates School entity from UpdateSchoolDto
     */
    public void updateEntityFromDto(UpdateSchoolDto updateSchoolDto, School school) {
        modelMapper.map(updateSchoolDto, school);
    }

    /**
     * Converts list of School entities to list of SchoolDtos
     */
    public List<SchoolDto> toDtoList(List<School> schools) {
        return schools.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}