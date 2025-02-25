package edu.manipal.cse.questionbankservice.services.impl;

import edu.manipal.cse.questionbankservice.dto.request.CreateAnswerRequest;
import edu.manipal.cse.questionbankservice.dto.response.AnswerResponse;
import edu.manipal.cse.questionbankservice.entities.Answer;
import edu.manipal.cse.questionbankservice.entities.Question;
import edu.manipal.cse.questionbankservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.questionbankservice.mappers.AnswerMapper;
import edu.manipal.cse.questionbankservice.repositories.AnswerRepository;
import edu.manipal.cse.questionbankservice.repositories.QuestionRepository;
import edu.manipal.cse.questionbankservice.services.AnswerService;
import edu.manipal.cse.questionbankservice.utils.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    private static final String ANSWER_CACHE = "answer";
    private static final String ANSWER_LIST_CACHE = "answerList";

    @Override
    @Transactional
    @CacheEvict(cacheNames = ANSWER_LIST_CACHE, key = "#questionId")
    public AnswerResponse createAnswer(CreateAnswerRequest request, UUID questionId) {
        log.info("Creating answer for question ID: {}", questionId);
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException(Messages.QUESTION_NOT_FOUND + questionId));

        Answer answer = answerMapper.toEntity(request);
        answer.setQuestion(question);

        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer created with ID: {}", savedAnswer.getAnswerId());
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    @Cacheable(cacheNames = ANSWER_LIST_CACHE, key = "#questionId", unless = "#result.isEmpty()")
    public List<AnswerResponse> getAllAnswersForQuestion(UUID questionId) {
        log.info("Fetching all answers for question ID: {}", questionId);
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(questionId);
        return answerMapper.toResponseList(answers);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {ANSWER_CACHE, ANSWER_LIST_CACHE}, allEntries = true)
    public AnswerResponse updateAnswer(UUID answerId, CreateAnswerRequest request) {
        log.info("Updating answer with ID: {}", answerId);
        Answer answer = findAnswerOrThrowException(answerId);

        answerMapper.updateEntity(request, answer);
        Answer updatedAnswer = answerRepository.save(answer);
        log.info("Answer updated with ID: {}", updatedAnswer.getAnswerId());
        return answerMapper.toResponse(updatedAnswer);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {ANSWER_CACHE, ANSWER_LIST_CACHE}, allEntries = true)
    public void deleteAnswer(UUID answerId) {
        log.info("Deleting answer with ID: {}", answerId);
        Answer answer = findAnswerOrThrowException(answerId);
        answerRepository.delete(answer);
    }

    @Override
    @Cacheable(cacheNames = ANSWER_CACHE, key = "#answerId", unless = "#result == null")
    public AnswerResponse getAnswer(UUID answerId) {
        log.info("Fetching answer with ID: {}", answerId);
        Answer answer = findAnswerOrThrowException(answerId);
        return answerMapper.toResponse(answer);
    }

    private Answer findAnswerOrThrowException(UUID answerId) {
        return answerRepository
                .findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException(Messages.ANSWER_NOT_FOUND + answerId));
    }
}