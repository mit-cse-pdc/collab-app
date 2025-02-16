package com.pdc.masterdataservice.configs;

import com.pdc.masterdataservice.dto.CourseDto;
import com.pdc.masterdataservice.dto.EnrollmentDto;
import com.pdc.masterdataservice.dto.FacultyCourseDto;
import com.pdc.masterdataservice.dto.SpecializationDto;
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
        modelMapper.createTypeMap(Course.class, CourseDto.class)
                .addMapping(Course::getCourseId, CourseDto::setCourseId)
                .addMapping(Course::getName, CourseDto::setName);

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
