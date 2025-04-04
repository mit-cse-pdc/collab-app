package edu.manipal.cse.lectureservicereactive.services.impl;

import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.UpdateLectureQuestionStatusInput;
import edu.manipal.cse.lectureservicereactive.exceptions.InvalidStatusTransitionException;
import edu.manipal.cse.lectureservicereactive.exceptions.OperationFailedException;
import edu.manipal.cse.lectureservicereactive.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.repositories.LectureQuestionRepository;
import edu.manipal.cse.lectureservicereactive.services.LectureQuestionService;
import edu.manipal.cse.lectureservicereactive.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureQuestionServiceImpl implements LectureQuestionService {

    private final LectureQuestionRepository lectureQuestionRepository;
    private final SubscriptionService subscriptionService;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<LectureQuestion> findLectureQuestionById(UUID lectureQuestionId) {
        log.debug("Finding lecture question by ID: {}", lectureQuestionId);
        return lectureQuestionRepository.findById(lectureQuestionId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("LectureQuestion", lectureQuestionId)));
    }

    @Override
    public Flux<LectureQuestion> findQuestionsForLecture(UUID lectureId) {
        log.debug("Finding lecture questions for lecture ID: {}", lectureId);
        return lectureQuestionRepository.findByLectureId(lectureId);
    }

    @Override
    public Mono<LectureQuestion> updateStatus(UpdateLectureQuestionStatusInput input) {
        log.info("Updating status for lecture question ID: {} to {}", input.lectureQuestionId(), input.status());
        return lectureQuestionRepository.findById(input.lectureQuestionId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("LectureQuestion", input.lectureQuestionId())))
                .flatMap(lq -> {
                    if (!isValidStatusTransition(lq.getStatus(), input.status())) {
                        log.warn("Invalid status transition from {} to {} for lecture question {}", lq.getStatus(), input.status(), input.lectureQuestionId());
                        return Mono.error(new InvalidStatusTransitionException(
                                "Cannot transition lecture question from " + lq.getStatus() + " to " + input.status()));
                    }
                    log.debug("Updating lecture question {} status from {} to {}", lq.getLectureQuestionId(), lq.getStatus(), input.status());
                    lq.setStatus(input.status());
                    return lectureQuestionRepository.save(lq);
                })
                .as(transactionalOperator::transactional)
                .doOnSuccess(updatedLq -> {
                    log.info("Successfully updated lecture question status for ID: {}. Publishing event.", updatedLq.getLectureQuestionId());
                    subscriptionService.publishLectureQuestionUpdate(updatedLq);
                })
                .doOnError(error -> log.error("Error updating lecture question status: {}", error.getMessage(), error))
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException || e instanceof InvalidStatusTransitionException),
                        e -> new OperationFailedException("Failed to update lecture question status", e));
    }

    private boolean isValidStatusTransition(LectureQuestion.LectureQuestionStatus current, LectureQuestion.LectureQuestionStatus next) {
        // Example: Allow PENDING -> ACTIVE -> COMPLETED, but not backwards
        if (current == LectureQuestion.LectureQuestionStatus.COMPLETED && next != LectureQuestion.LectureQuestionStatus.COMPLETED) return false;
        if (current == LectureQuestion.LectureQuestionStatus.ACTIVE && next == LectureQuestion.LectureQuestionStatus.PENDING) return false;
        return true;
    }
}