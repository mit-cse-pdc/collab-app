package com.pdc.masterdataservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Create Faculty Course request")
public class CreateFacultyCourseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Faculty ID is required")
    private UUID facultyId;

    @NotNull(message = "Course ID is required")
    private UUID courseId;
}
