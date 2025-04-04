package edu.manipal.cse.lectureservicereactive.services.impl;

import edu.manipal.cse.lectureservicereactive.clients.AnswerServiceClient;
import edu.manipal.cse.lectureservicereactive.dto.inputs.studentResponse.CreateStudentResponseInput;
import edu.manipal.cse.lectureservicereactive.dto.outputs.ResponseCount;
import edu.manipal.cse.lectureservicereactive.exceptions.DuplicateResourceException;
import edu.manipal.cse.lectureservicereactive.exceptions.OperationFailedException;
import edu.manipal.cse.lectureservicereactive.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import edu.manipal.cse.lectureservicereactive.repositories.LectureQuestionRepository;
import edu.manipal.cse.lectureservicereactive.repositories.StudentResponseRepository;
import edu.manipal.cse.lectureservicereactive.services.StudentResponseService;
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
public class StudentResponseServiceImpl implements StudentResponseService {

    private final SubscriptionService subscriptionService;
    private final StudentResponseRepository studentResponseRepository;
    private final LectureQuestionRepository lectureQuestionRepository;
    private final AnswerServiceClient answerServiceClient;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<StudentResponse> findResponseById(UUID responseId) {
        log.debug("Finding student response by ID: {}", responseId);
        return studentResponseRepository.findById(responseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("StudentResponse", responseId)));
    }

    @Override
    public Flux<StudentResponse> findResponsesForLectureQuestion(UUID lectureQuestionId) {
        log.debug("Finding student responses for lecture question ID: {}", lectureQuestionId);
        return studentResponseRepository.findByLectureQuestionId(lectureQuestionId);
    }

    @Override
    public Flux<StudentResponse> findResponsesByStudent(UUID studentId) {
        log.debug("Finding student responses for student ID: {}", studentId);
        return studentResponseRepository.findByStudentId(studentId);
    }

    @Override
    public Mono<StudentResponse> createStudentResponse(CreateStudentResponseInput input, UUID studentId) {
        UUID lectureQuestionId = input.lectureQuestionId();
        UUID answerId = input.answerId();
        log.info("Creating student response for student {} on lecture question {} with answer {}", studentId, lectureQuestionId, answerId);

        // --- Phase 1: Validation ---
        Mono<Boolean> lectureQuestionExistsMono = lectureQuestionRepository.existsById(lectureQuestionId)
                .doOnSuccess(exists -> { if (!exists) log.warn("Validation failed: LectureQuestion {} not found", lectureQuestionId); });

        Mono<Boolean> answerExistsMono = answerServiceClient.answerExists(answerId)
                .doOnSuccess(exists -> { if (!exists) log.warn("Validation failed: Answer {} not found", answerId); });

        Mono<Boolean> alreadyRespondedMono = studentResponseRepository.existsByStudentIdAndLectureQuestionId(studentId, lectureQuestionId)
                .map(exists -> !exists)
                .doOnSuccess(canRespond -> { if (!canRespond) log.warn("Validation failed: Student {} already responded to LectureQuestion {}", studentId, lectureQuestionId); });


        // Combine all validations
        return Mono.zip(lectureQuestionExistsMono, answerExistsMono, alreadyRespondedMono)
                .flatMap(tuple -> {
                    boolean lqExists = tuple.getT1();
                    boolean answerExists = tuple.getT2();
                    boolean canRespond = tuple.getT3();

                    // Check validation results
                    if (!lqExists) {
                        return Mono.error(new ResourceNotFoundException("LectureQuestion", lectureQuestionId));
                    }
                    if (!answerExists) {
                        return Mono.error(new ResourceNotFoundException("Answer", answerId));
                    }
                    if (!canRespond) {
                        return Mono.error(new DuplicateResourceException(
                                "Student has already submitted a response for this question."));
                    }

                    // --- Phase 2: Transactional Save ---
                    return Mono.defer(() -> {
                                StudentResponse response = StudentResponse.builder()
                                        .studentId(studentId)
                                        .lectureQuestionId(lectureQuestionId)
                                        .answerId(answerId)
                                        .build();
                                log.debug("Saving new student response: {}", response);
                                return studentResponseRepository.save(response);
                            })
                            .as(transactionalOperator::transactional);
                })
                // --- Phase 3: Publish Events (after successful save) ---
                .doOnSuccess(savedResponse -> {
                    log.info("Successfully saved student response {}. Publishing individual event.", savedResponse.getResponseId());
                    subscriptionService.publishStudentResponseCreated(savedResponse);
                })
                .delayUntil(savedResponse ->
                        studentResponseRepository.countByLectureQuestionId(lectureQuestionId)
                                .doOnSuccess(count -> {
                                    log.info("Publishing response count update for LQI {}: {}", lectureQuestionId, count);
                                    subscriptionService.publishResponseCountUpdate(new ResponseCount(lectureQuestionId, count.intValue()));
                                })
                                .doOnError(e -> log.error("Failed to get count or publish count update for LQI {}", lectureQuestionId, e))
                                .then()
                )
                // --- Overall Error Handling ---
                .doOnError(error -> log.error("Error creating student response: {}", error.getMessage(), error))
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException || e instanceof DuplicateResourceException ),
                        e -> new OperationFailedException("Failed to create student response", e));
    }

    @Override
    public Mono<Integer> countResponsesForLectureQuestion(UUID lectureQuestionId) {
        log.debug("Counting student responses for lecture question ID: {}", lectureQuestionId);
        return studentResponseRepository
                .countByLectureQuestionId(lectureQuestionId)
                .map(Long::intValue);
    }
}