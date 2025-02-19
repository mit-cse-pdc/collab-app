package com.pdc.questionbankservice.services;

import com.pdc.questionbankservice.dto.request.CreateAnswerRequest;
import com.pdc.questionbankservice.dto.response.AnswerResponse;

import java.util.List;
import java.util.UUID;

public interface AnswerService {
    AnswerResponse createAnswer(CreateAnswerRequest request, UUID questionId);
    List<AnswerResponse> getAllAnswersForQuestion(UUID questionId);
    AnswerResponse updateAnswer(UUID answerId, CreateAnswerRequest request);
    void deleteAnswer(UUID answerId);
    AnswerResponse getAnswer(UUID answerId);
}