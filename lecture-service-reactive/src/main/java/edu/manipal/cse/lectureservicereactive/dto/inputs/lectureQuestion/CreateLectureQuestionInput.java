package edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLectureQuestionInput(
        @NotNull UUID questionId
) {}
