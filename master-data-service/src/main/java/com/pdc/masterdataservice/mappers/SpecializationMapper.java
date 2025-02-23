package com.pdc.masterdataservice.mappers;

import com.pdc.masterdataservice.dto.response.SpecializationDto;
import com.pdc.masterdataservice.dto.request.CreateSpecializationDto;
import com.pdc.masterdataservice.dto.request.UpdateSpecializationDto;
import com.pdc.masterdataservice.entities.School;
import com.pdc.masterdataservice.entities.Specialization;
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