package com.pdc.masterdataservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pdc.masterdataservice.entities.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Course DTO for responses")
public class CourseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID courseId;
    private String courseCode;
    private String name;
    private String description;
    private Integer credits;
    private Integer semester;
    private Integer academicYear;
    private UUID specializationId;

    private Course.CourseStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}