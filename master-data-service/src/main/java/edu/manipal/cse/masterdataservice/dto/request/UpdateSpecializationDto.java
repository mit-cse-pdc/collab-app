package edu.manipal.cse.masterdataservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSpecializationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Specialization name is required")
    private String name;
}