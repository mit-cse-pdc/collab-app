package edu.manipal.cse.lectureservicereactive.dto.client.questions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse implements Serializable {
    private UUID answerId;
    private String text;
    private Boolean isCorrect;
    private String explanation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
