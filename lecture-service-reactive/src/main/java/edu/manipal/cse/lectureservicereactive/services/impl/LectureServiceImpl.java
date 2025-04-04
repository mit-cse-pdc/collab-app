package edu.manipal.cse.lectureservicereactive.services.impl;


import edu.manipal.cse.lectureservicereactive.clients.ChapterServiceClient;
import edu.manipal.cse.lectureservicereactive.clients.QuestionServiceClient;
import edu.manipal.cse.lectureservicereactive.dto.events.LectureCreatedEvent;
import edu.manipal.cse.lectureservicereactive.dto.events.LectureUpdatedEvent;
import edu.manipal.cse.lectureservicereactive.dto.inputs.lecture.*;
import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.CreateLectureQuestionInput;
import edu.manipal.cse.lectureservicereactive.exceptions.DuplicateResourceException;
import edu.manipal.cse.lectureservicereactive.exceptions.InvalidStatusTransitionException;
import edu.manipal.cse.lectureservicereactive.exceptions.OperationFailedException;
import edu.manipal.cse.lectureservicereactive.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.repositories.LectureQuestionRepository;
import edu.manipal.cse.lectureservicereactive.repositories.LectureRepository;
import edu.manipal.cse.lectureservicereactive.services.LectureService;
import edu.manipal.cse.lectureservicereactive.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureServiceImpl implements LectureService {

    private final SubscriptionService subscriptionService;
    private final LectureRepository lectureRepository;
    private final QuestionServiceClient questionServiceClient;
    private final ChapterServiceClient chapterServiceClient;
    private final LectureQuestionRepository lectureQuestionRepository;
    private final TransactionalOperator transactionalOperator;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<Lecture> findLectureById(UUID lectureId) {
        log.debug("Finding lecture by ID: {}", lectureId);
        return lectureRepository.findById(lectureId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Lecture", lectureId)));
    }

    @Override
    public Flux<Lecture> findAllLectures(LectureFilterInput filter) {
        log.debug("Finding all lectures with filter: {}", filter);
        return (filter != null && filter.status() != null) ?
                lectureRepository.findByStatus(filter.status()) :
                lectureRepository.findAll();
    }

    @Override
    public Mono<Lecture> createLecture(CreateLectureInput input) {
        log.info("Initiating creation for lecture: {}", input.title());

        Mono<Boolean> chapterValidationMono = chapterServiceClient.chapterExists(input.chapterId())
                .doOnSuccess(exists -> {
                    if (!exists) log.warn("Validation failed: Chapter {} not found", input.chapterId());
                })
                .onErrorResume(e -> {
                    log.error("Error validating chapter {}: {}", input.chapterId(), e.getMessage());
                    return Mono.just(false);
                });

        Mono<Boolean> questionsValidationMono;
        List<CreateLectureQuestionInput> questionInputs = input.lectureQuestions();
        final List<UUID> questionIds = (questionInputs != null && !questionInputs.isEmpty())
                ? questionInputs.stream().map(CreateLectureQuestionInput::questionId).collect(Collectors.toList())
                : Collections.emptyList();

        if (!questionIds.isEmpty()) {
            log.debug("Validating existence of question IDs: {}", questionIds);
            questionsValidationMono = questionServiceClient.validateAllQuestions(questionIds)
                    .map(apiResponse -> apiResponse.getData() != null && apiResponse.getData().size() == questionIds.size())
                    .doOnSuccess(allValid -> {
                        if (!allValid) log.warn("Validation failed: Not all question IDs {} were valid.", questionIds);
                    })
                    .onErrorReturn(false);
        } else {
            questionsValidationMono = Mono.just(true);
        }

        return Mono.zip(chapterValidationMono, questionsValidationMono)
                .flatMap(validationResults -> {
                    boolean chapterExists = validationResults.getT1();
                    boolean questionsValid = validationResults.getT2();

                    if (!chapterExists) {
                        return Mono.error(new ResourceNotFoundException("Chapter", input.chapterId()));
                    }
                    if (!questionsValid) {
                        return Mono.error(new OperationFailedException("One or more Question IDs are invalid or validation failed."));
                    }

                    log.info("External validations passed for lecture: {}. Proceeding with save.", input.title());

                    return Mono.defer(() -> {
                        Lecture lecture = new Lecture();
                        lecture.setFacultyId(input.facultyId());
                        lecture.setChapterId(input.chapterId());
                        lecture.setTitle(input.title());
                        lecture.setStatus(Lecture.LectureStatus.SCHEDULED);

                        return lectureRepository.save(lecture)
                                .flatMap(savedLecture -> {
                                    log.info("Saved lecture with ID: {}", savedLecture.getLectureId());
                                    if (!questionIds.isEmpty()) {
                                        List<LectureQuestion> questionsToSave = questionIds.stream()
                                                .map(qId -> {
                                                    LectureQuestion lq = new LectureQuestion();
                                                    lq.setLectureId(savedLecture.getLectureId());
                                                    lq.setQuestionId(qId);
                                                    return lq;
                                                })
                                                .collect(Collectors.toList());

                                        log.debug("Saving {} lecture questions for lecture ID: {}", questionsToSave.size(), savedLecture.getLectureId());
                                        return lectureQuestionRepository.saveAll(questionsToSave)
                                                .collectList()
                                                .thenReturn(savedLecture);
                                    } else {
                                        return Mono.just(savedLecture);
                                    }
                                });
                    }).as(transactionalOperator::transactional);
                })
                .doOnSuccess(savedLecture -> {
                    log.info("Successfully created lecture ID: {}. Publishing events.", savedLecture.getLectureId());
                     subscriptionService.publishLectureEvent(new LectureCreatedEvent(savedLecture));
                     subscriptionService.publishLectureUpdate(savedLecture);
                })
                .doOnError(error -> log.error("Error during createLecture flow: {}", error.getMessage(), error))
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException || e instanceof OperationFailedException),
                        e -> new OperationFailedException("Failed to create lecture due to an unexpected error", e));
    }

    @Override
    public Mono<Lecture> updateLectureStatus(UpdateLectureStatusInput input) {
        log.info("Updating status for lecture ID: {} to {}", input.lectureId(), input.status());
        return lectureRepository.findById(input.lectureId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Lecture", input.lectureId())))
                .flatMap(lecture -> {
                    if (!isValidStatusTransition(lecture.getStatus(), input.status())) {
                        log.warn("Invalid status transition from {} to {} for lecture {}", lecture.getStatus(), input.status(), input.lectureId());
                        return Mono.error(new InvalidStatusTransitionException(
                                "Cannot transition lecture from " + lecture.getStatus() + " to " + input.status()));
                    }
                    log.debug("Updating lecture {} status from {} to {}", lecture.getLectureId(), lecture.getStatus(), input.status());
                    lecture.setStatus(input.status());
                    return lectureRepository.save(lecture);
                })
                .as(transactionalOperator::transactional)
                .doOnSuccess(updatedLecture -> {
                    log.info("Publishing update events for lecture ID: {}", updatedLecture.getLectureId());
                    subscriptionService.publishLectureEvent(new LectureUpdatedEvent(updatedLecture));
                    subscriptionService.publishLectureUpdate(updatedLecture);
                })
                .doOnError(error -> log.error("Error updating lecture status: {}", error.getMessage(), error))
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException || e instanceof InvalidStatusTransitionException),
                        e -> new OperationFailedException("Failed to update lecture status", e));
    }

    @Override
    public Mono<Lecture> addQuestionsToLecture(AddQuestionsToLectureInput input) {
        log.info("Adding questions to lecture ID: {}", input.lectureId());
        return lectureRepository.findById(input.lectureId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Lecture", input.lectureId())))
                .flatMap(lecture -> {
                    List<LectureQuestion> questionsToSave = input.questions().stream()
                            .map(qInput -> {
                                LectureQuestion lq = new LectureQuestion();
                                lq.setLectureId(lecture.getLectureId()); // Set FK
                                lq.setQuestionId(qInput.questionId());
                                return lq;
                            })
                            .collect(Collectors.toList());

                    log.debug("Adding {} new lecture questions for lecture ID: {}", questionsToSave.size(), lecture.getLectureId());
                    return lectureQuestionRepository.saveAll(questionsToSave)
                            .collectList()
                            .thenReturn(lecture);
                })
                .as(transactionalOperator::transactional)
                .doOnSuccess(updatedLecture -> {
                    log.info("Publishing update event after adding questions to lecture ID: {}", updatedLecture.getLectureId());
                    subscriptionService.publishLectureEvent(new LectureUpdatedEvent(updatedLecture));
                    subscriptionService.publishLectureUpdate(updatedLecture);
                })
                .doOnError(error -> log.error("Error adding questions to lecture: {}", error.getMessage(), error))
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException || e instanceof DuplicateResourceException),
                        e -> new OperationFailedException("Failed to add questions to lecture", e));
    }

    @Override
    public Mono<Lecture> removeQuestionsFromLecture(RemoveQuestionsFromLectureInput input) {
        log.info("Removing {} questions from lecture ID: {}", input.lectureQuestionIds().size(), input.lectureId());
        return lectureRepository.findById(input.lectureId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Lecture", input.lectureId())))
                .flatMap(lecture -> lectureQuestionRepository
                        .deleteAllById(input.lectureQuestionIds())
                        .then(Mono.just(lecture))
                )
                .doOnSuccess(updatedLecture -> {
                    log.info("Publishing update event after removing questions from lecture ID: {}", updatedLecture.getLectureId());
                    subscriptionService.publishLectureEvent(new LectureUpdatedEvent(updatedLecture));
                    subscriptionService.publishLectureUpdate(updatedLecture);
                })
                .as(transactionalOperator::transactional)
                .doOnError(error -> log.error("Error removing questions from lecture: {}", error.getMessage(), error))
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new OperationFailedException("Failed to remove questions from lecture", e));
    }

    @Override
    public Mono<Long> countLectures(LectureFilterInput filter) {
        log.debug("Counting lectures with filter: {}", filter);

        Criteria criteria = Criteria.empty();

        if (filter != null) {
            List<Criteria> criteriaList = new ArrayList<>();
            if (filter.facultyId() != null) {
                criteriaList.add(Criteria.where("faculty_id").is(filter.facultyId()));
            }
            if (filter.chapterId() != null) {
                criteriaList.add(Criteria.where("chapter_id").is(filter.chapterId()));
            }
            if (filter.status() != null) {
                criteriaList.add(Criteria.where("status").is(filter.status().name()));
            }
            if (!criteriaList.isEmpty()) {
                criteria = criteriaList.stream().reduce(Criteria.empty(), Criteria::and);
            }
        }

        Query query = Query.query(criteria);
        return r2dbcEntityTemplate.count(query, Lecture.class);
    }

    @Override
    public Flux<Lecture> findLectures(LectureFilterInput filter, Pageable pageable) {
        log.debug("Finding lectures with filter: {}, pageable: {}", filter, pageable);

        Criteria criteria = buildCriteriaFromFilter(filter);
        Query query = Query.query(criteria).with(pageable);
        return r2dbcEntityTemplate.select(query, Lecture.class);
    }

    private Criteria buildCriteriaFromFilter(LectureFilterInput filter) {
        Criteria criteria = Criteria.empty();
        if (filter != null) {
            List<Criteria> criteriaList = new ArrayList<>();
            if (filter.facultyId() != null) {
                criteriaList.add(Criteria.where("faculty_id").is(filter.facultyId()));
            }
            if (filter.chapterId() != null) {
                criteriaList.add(Criteria.where("chapter_id").is(filter.chapterId()));
            }
            if (filter.status() != null) {
                criteriaList.add(Criteria.where("status").is(filter.status().name()));
            }
            if (!criteriaList.isEmpty()) {
                criteria = criteriaList.stream().reduce(Criteria.empty(), Criteria::and);
            }
        }
        return criteria;
    }


    private boolean isValidStatusTransition(Lecture.LectureStatus current, Lecture.LectureStatus next) {
        // Example: Allow moving forward, or to cancelled (but not back from completed/cancelled unless specific logic allows)
        if (current == Lecture.LectureStatus.COMPLETED && next != Lecture.LectureStatus.COMPLETED) return false;
        if (current == Lecture.LectureStatus.CANCELLED && next != Lecture.LectureStatus.CANCELLED) return false;
        return true;
    }
}