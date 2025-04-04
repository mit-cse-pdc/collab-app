package edu.manipal.cse.lectureservicereactive.dto.inputs.lecture;

import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.CreateLectureQuestionInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateLectureInput(
        @NotNull UUID facultyId,
        @NotNull UUID chapterId,
        @NotBlank @Size(max = 255) String title,
        @Valid List<CreateLectureQuestionInput> lectureQuestions
) {}