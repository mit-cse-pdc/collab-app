package edu.manipal.cse.masterdataservice.mappers;

import edu.manipal.cse.masterdataservice.dto.response.SpecializationDto;
import edu.manipal.cse.masterdataservice.dto.request.CreateSpecializationDto;
import edu.manipal.cse.masterdataservice.dto.request.UpdateSpecializationDto;
import edu.manipal.cse.masterdataservice.entities.School;
import edu.manipal.cse.masterdataservice.entities.Specialization;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SpecializationMapper {

    private final ModelMapper modelMapper;

    public SpecializationDto toDto(Specialization specialization) {
        return modelMapper.map(specialization, SpecializationDto.class);
    }

    public Specialization toEntity(CreateSpecializationDto dto, School school) {
        Specialization specialization = modelMapper.map(dto, Specialization.class);
        specialization.setSchool(school);
        return specialization;
    }

    public void updateEntityFromDto(UpdateSpecializationDto dto, Specialization specialization) {
        modelMapper.map(dto, specialization);
    }

    public List<SpecializationDto> toDtoList(List<Specialization> specializations) {
        return specializations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}