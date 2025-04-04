package edu.manipal.cse.lectureservicereactive.dto.inputs.lecture;

import edu.manipal.cse.lectureservicereactive.models.Lecture;

import java.util.UUID;

public record LectureFilterInput(
        UUID facultyId,
        UUID chapterId,
        Lecture.LectureStatus status
) {}
