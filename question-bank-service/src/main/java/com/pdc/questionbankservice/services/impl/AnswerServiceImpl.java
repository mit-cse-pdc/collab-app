package com.pdc.questionbankservice.services.impl;

import com.pdc.questionbankservice.dto.request.CreateAnswerRequest;
import com.pdc.questionbankservice.dto.response.AnswerResponse;
import com.pdc.questionbankservice.entities.Answer;
import com.pdc.questionbankservice.entities.Question;
import com.pdc.questionbankservice.exceptions.ResourceNotFoundException;
import com.pdc.questionbankservice.mappers.AnswerMapper;
import com.pdc.questionbankservice.repositories.AnswerRepository;
import com.pdc.questionbankservice.repositories.QuestionRepository;
import com.pdc.questionbankservice.services.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    @Override
    @Transactional
    public AnswerResponse createAnswer(CreateAnswerRequest request, UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));

        Answer answer = answerMapper.toEntity(request);
        answer.setQuestion(question);
        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    public List<AnswerResponse> getAllAnswersForQuestion(UUID questionId) {
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(questionId);
        return answers.stream()
                .map(answerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnswerResponse updateAnswer(UUID answerId, CreateAnswerRequest request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));

        answerMapper.updateEntity(request, answer);
        Answer updatedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(updatedAnswer);
    }

    @Override
    @Transactional
    public void deleteAnswer(UUID answerId) {
        if (answerRepository.existsById(answerId)) {
            answerRepository.deleteById(answerId);
        } else {
            throw new ResourceNotFoundException("Answer not found with ID: " + answerId);
        }
    }

    @Override
    public AnswerResponse getAnswer(UUID answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));
        return answerMapper.toResponse(answer);
    }
}