package edu.manipal.cse.lectureservicereactive.dto.inputs.lecture;

import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.CreateLectureQuestionInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AddQuestionsToLectureInput(
        @NotNull UUID lectureId,
        @NotEmpty @Valid List<@NotNull CreateLectureQuestionInput> questions
) {}