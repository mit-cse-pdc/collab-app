package edu.manipal.cse.userservice.dto.request;

import edu.manipal.cse.userservice.entities.Faculty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FacultyUpdateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password; // Optional for update

    @NotNull(message = "Position is required")
    private Faculty.Position position;
}
