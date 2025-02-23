package com.pdc.masterdataservice.configs;

import com.pdc.masterdataservice.dto.response.CourseDto;
import com.pdc.masterdataservice.dto.response.EnrollmentDto;
import com.pdc.masterdataservice.dto.response.FacultyCourseDto;
import com.pdc.masterdataservice.dto.response.SpecializationDto;
import com.pdc.masterdataservice.entities.Course;
import com.pdc.masterdataservice.entities.Enrollment;
import com.pdc.masterdataservice.entities.FacultyCourse;
import com.pdc.masterdataservice.entities.Specialization;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setPropertyCondition(context -> context.getSource() != null);

        // Configure Course mapping
        modelMapper.typeMap(Course.class, CourseDto.class)
                .addMappings(mapper ->
                        mapper.map(src -> src.getSpecialization().getSpecializationId(),
                                CourseDto::setSpecializationId)
                );

        // Configure FacultyCourse mapping
        modelMapper.typeMap(FacultyCourse.class, FacultyCourseDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getCourse().getCourseId(), FacultyCourseDto::setCourseId);
                    mapper.map(src -> src.getCourse().getName(), FacultyCourseDto::setCourseName);
                });

        // Configure Enrollment mapping
        modelMapper.typeMap(Enrollment.class, EnrollmentDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getCourse().getCourseId(), EnrollmentDto::setCourseId);
                    mapper.map(src -> src.getCourse().getName(), EnrollmentDto::setCourseName);
                });

        // Configure Specialization mapping
        modelMapper.typeMap(Specialization.class, SpecializationDto.class)
                .addMapping(src -> src.getSchool().getSchoolId(), SpecializationDto::setSchoolId);

        return modelMapper;
    }
}
