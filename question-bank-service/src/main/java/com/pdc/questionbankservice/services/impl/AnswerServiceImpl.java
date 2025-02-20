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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"answer", "answerList"}, allEntries = true)
    public AnswerResponse createAnswer(CreateAnswerRequest request, UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));

        Answer answer = answerMapper.toEntity(request);
        answer.setQuestion(question);
        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    @Cacheable(cacheNames = "answerList", key = "#questionId")
    public List<AnswerResponse> getAllAnswersForQuestion(UUID questionId) {
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(questionId);
        return answers
                .stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"answer", "answerList"}, allEntries = true)
    public AnswerResponse updateAnswer(UUID answerId, CreateAnswerRequest request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));

        answerMapper.updateEntity(request, answer);
        Answer updatedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(updatedAnswer);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"answer", "answerList"}, allEntries = true)
    public void deleteAnswer(UUID answerId) {
        if (answerRepository.existsById(answerId)) {
            answerRepository.deleteById(answerId);
        } else {
            throw new ResourceNotFoundException("Answer not found with ID: " + answerId);
        }
    }

    @Override
    @Cacheable(cacheNames = "answer", key = "#answerId")
    public AnswerResponse getAnswer(UUID answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));
        return answerMapper.toResponse(answer);
    }
}