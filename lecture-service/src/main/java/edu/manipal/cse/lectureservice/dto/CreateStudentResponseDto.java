package edu.manipal.cse.lectureservice.dto;

import java.util.UUID;

public record CreateStudentResponseDto(
        UUID lectureId,
        UUID questionId,
        UUID optionId
) {
}
