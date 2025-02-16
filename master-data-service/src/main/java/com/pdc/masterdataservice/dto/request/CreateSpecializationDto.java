package com.pdc.masterdataservice.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateSpecializationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Specialization name is required")
    private String name;

    @NotNull(message = "School ID is required")
    private UUID schoolId;
}