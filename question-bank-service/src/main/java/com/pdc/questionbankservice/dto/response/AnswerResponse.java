package com.pdc.questionbankservice.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AnswerResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID answerId;
    private String text;
    private Boolean isCorrect;
    private String explanation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
