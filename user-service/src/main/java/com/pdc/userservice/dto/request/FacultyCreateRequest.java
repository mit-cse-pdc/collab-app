package com.pdc.userservice.dto.request;

import com.pdc.userservice.entities.enums.Position;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class FacultyCreateRequest {
    @NotNull(message = "School ID is required")
    private UUID schoolId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Position is required")
    private Position position;
}
