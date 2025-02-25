package edu.manipal.cse.questionbankservice.services;

import edu.manipal.cse.questionbankservice.dto.request.CreateAnswerRequest;
import edu.manipal.cse.questionbankservice.dto.response.AnswerResponse;

import java.util.List;
import java.util.UUID;

public interface AnswerService {
    AnswerResponse createAnswer(CreateAnswerRequest request, UUID questionId);
    List<AnswerResponse> getAllAnswersForQuestion(UUID questionId);
    AnswerResponse updateAnswer(UUID answerId, CreateAnswerRequest request);
    void deleteAnswer(UUID answerId);
    AnswerResponse getAnswer(UUID answerId);
}