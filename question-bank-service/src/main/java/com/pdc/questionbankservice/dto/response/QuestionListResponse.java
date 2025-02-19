package com.pdc.questionbankservice.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
public class QuestionListResponse {
    private List<QuestionResponse> questions;
    private int totalQuestions;
}
