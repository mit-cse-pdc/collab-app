package edu.manipal.cse.lectureservicereactive.dto.inputs.lecture;

import edu.manipal.cse.lectureservicereactive.models.Lecture;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateLectureStatusInput(
        @NotNull UUID lectureId,
        @NotNull Lecture.LectureStatus status
) {}
