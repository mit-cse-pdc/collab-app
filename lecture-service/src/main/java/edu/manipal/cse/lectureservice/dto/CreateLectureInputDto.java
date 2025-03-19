package edu.manipal.cse.lectureservice.dto;

import java.util.List;
import java.util.UUID;

public record CreateLectureInputDto(
        UUID facultyId,
        UUID chapterId,
        String title,
        List<UUID> lectureQuestions
) {
}
