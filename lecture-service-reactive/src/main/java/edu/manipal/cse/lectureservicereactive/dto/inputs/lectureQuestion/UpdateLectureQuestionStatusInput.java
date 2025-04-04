package edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion;

import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateLectureQuestionStatusInput(
        @NotNull UUID lectureQuestionId,
        @NotNull LectureQuestion.LectureQuestionStatus status
) {}