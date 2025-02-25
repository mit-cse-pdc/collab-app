package edu.manipal.cse.authservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDTO {
    private UUID facultyId;
    private UUID schoolId;
    private String email;
    private String password;
    private String name;
    private Position position;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public enum Position {
        PROFESSOR,
        ASSOCIATE_PROFESSOR,
        ASSISTANT_PROFESSOR,
        ADDITIONAL_PROFESSOR,
        PROFESSOR_OF_PRACTICE
    }
}