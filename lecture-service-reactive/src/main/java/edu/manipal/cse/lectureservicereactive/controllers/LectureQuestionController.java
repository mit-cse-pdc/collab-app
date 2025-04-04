package edu.manipal.cse.lectureservicereactive.controllers;

import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.LectureQuestionFilterInput;
import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.UpdateLectureQuestionStatusInput;
import edu.manipal.cse.lectureservicereactive.dto.payloads.PayloadHelper;
import edu.manipal.cse.lectureservicereactive.dto.payloads.UpdateLectureQuestionPayload;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.repositories.LectureRepository;
import edu.manipal.cse.lectureservicereactive.services.LectureQuestionService;
import edu.manipal.cse.lectureservicereactive.services.LectureService;
import edu.manipal.cse.lectureservicereactive.services.StudentResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LectureQuestionController {

    private final LectureQuestionService lectureQuestionService;
    private final LectureService lectureService;
    private final StudentResponseService studentResponseService;
    private final LectureRepository lectureRepository;

    @QueryMapping
    public Mono<LectureQuestion> getLectureQuestionById(@Argument UUID lectureQuestionId) {
        log.info("GraphQL query: getLectureQuestionById(id: {})", lectureQuestionId);
        return lectureQuestionService.findLectureQuestionById(lectureQuestionId);
    }

    @QueryMapping
    public Flux<LectureQuestion> getQuestionsForLecture(@Argument UUID lectureId, @Argument LectureQuestionFilterInput filter) {
        log.info("GraphQL query: getQuestionsForLecture(lectureId: {}, filter: {})", lectureId, filter);
        return lectureQuestionService.findQuestionsForLecture(lectureId);
    }

    @MutationMapping
    public Mono<UpdateLectureQuestionPayload> updateLectureQuestionStatus(@Argument @Valid UpdateLectureQuestionStatusInput input) {
        log.info("GraphQL mutation: updateLectureQuestionStatus(id: {}, status: {})", input.lectureQuestionId(), input.status());
        return lectureQuestionService.updateStatus(input)
                .map(lq -> new UpdateLectureQuestionPayload(lq, Collections.emptyList()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(new UpdateLectureQuestionPayload(null, PayloadHelper.mapExceptionToUserErrors(e)))
                );
    }

    @SchemaMapping(typeName = "LectureQuestion", field = "lecture")
    public Mono<Lecture> getLectureForQuestion(LectureQuestion lectureQuestion, DataLoader<UUID, Lecture> dataLoader) {
        log.debug("Resolving lecture for lectureQuestionId: {} using DataLoader", lectureQuestion.getLectureQuestionId());
        return Mono.fromFuture(dataLoader.load(lectureQuestion.getLectureId()));
    }

    @BatchMapping
    public Mono<Map<UUID, Lecture>> loadLecturesForQuestions(List<UUID> lectureIds) {
        log.debug("Batch loading Lectures for IDs: {}", lectureIds);
        return lectureRepository.findAllById(lectureIds)
                .collectMap(Lecture::getLectureId);
    }

    @SchemaMapping(typeName = "LectureQuestion", field = "studentResponseCount")
    public Mono<Integer> getStudentResponseCount(LectureQuestion lectureQuestion) {
        log.debug("Resolving studentResponseCount for lectureQuestionId: {}", lectureQuestion.getLectureQuestionId());
        return studentResponseService.countResponsesForLectureQuestion(lectureQuestion.getLectureQuestionId());
    }
}
