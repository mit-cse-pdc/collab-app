package edu.manipal.cse.lectureservicereactive.dto.inputs.lecture;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RemoveQuestionsFromLectureInput(
        @NotNull UUID lectureId,
        @NotEmpty List<@NotNull UUID> lectureQuestionIds
) {}