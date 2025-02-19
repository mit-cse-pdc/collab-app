package com.pdc.questionbankservice.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AnswerResponse {
    private UUID answerId;
    private String text;
    private Boolean isCorrect;
    private String explanation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
