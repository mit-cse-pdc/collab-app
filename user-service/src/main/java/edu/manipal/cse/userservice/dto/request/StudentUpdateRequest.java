package edu.manipal.cse.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentUpdateRequest {
    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^\\d{9}$", message = "Must be exactly a valid registration number")
    private String registrationNo; // Optional for update

    @NotBlank(message = "Name is required")
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password; // Optional for update
}