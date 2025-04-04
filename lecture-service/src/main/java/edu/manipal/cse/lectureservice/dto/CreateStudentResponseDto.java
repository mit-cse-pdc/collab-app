package edu.manipal.cse.lectureservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CreateStudentResponseDto(
        UUID lectureId,
        UUID questionId,
        UUID optionId
) {
    @JsonCreator
    public CreateStudentResponseDto(
            @JsonProperty("lectureId") String lectureId,
            @JsonProperty("questionId") String questionId,
            @JsonProperty("optionId") String optionId
    ) {
        this(
                UUID.fromString(lectureId),
                UUID.fromString(questionId),
                UUID.fromString(optionId)
        );
    }
}
