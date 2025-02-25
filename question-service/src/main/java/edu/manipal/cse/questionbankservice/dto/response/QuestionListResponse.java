package edu.manipal.cse.questionbankservice.dto.response;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class QuestionListResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<QuestionResponse> questions;
    private int totalQuestions;
}
