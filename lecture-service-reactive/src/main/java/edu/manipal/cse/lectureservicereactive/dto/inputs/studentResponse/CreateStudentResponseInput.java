package edu.manipal.cse.lectureservicereactive.dto.inputs.studentResponse;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStudentResponseInput(
        @NotNull UUID lectureQuestionId,
        @NotNull UUID answerId
) {}
