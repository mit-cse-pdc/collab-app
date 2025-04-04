package edu.manipal.cse.lectureservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateLectureQuestionInputDto(UUID questionId) {
    @JsonCreator
    public static CreateLectureQuestionInputDto fromJson(
            @JsonProperty("questionId") String questionId
    ) {
        return new CreateLectureQuestionInputDto(UUID.fromString(questionId));
    }
}