package edu.manipal.cse.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.manipal.cse.userservice.entities.Faculty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID facultyId;

    private UUID schoolId;
    private String email;
    private String password;

    private String name;
    private Faculty.Position position;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}