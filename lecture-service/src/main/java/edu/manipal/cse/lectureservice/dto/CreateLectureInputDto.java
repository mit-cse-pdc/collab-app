package edu.manipal.cse.lectureservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record CreateLectureInputDto(
        UUID facultyId,
        UUID chapterId,
        String title,
        List<CreateLectureQuestionInputDto> lectureQuestions
) {
    @JsonCreator
    public static CreateLectureInputDto fromJson(
            @JsonProperty("facultyId") String facultyId,
            @JsonProperty("chapterId") String chapterId,
            @JsonProperty("title") String title,
            @JsonProperty("lectureQuestions") List<CreateLectureQuestionInputDto> lectureQuestions
    ) {
        return new CreateLectureInputDto(
                UUID.fromString(facultyId),
                UUID.fromString(chapterId),
                title,
                lectureQuestions
        );
    }
}