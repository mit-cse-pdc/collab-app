package edu.manipal.cse.lectureservicereactive.controllers;

import edu.manipal.cse.lectureservicereactive.dto.inputs.studentResponse.CreateStudentResponseInput;
import edu.manipal.cse.lectureservicereactive.dto.payloads.CreateStudentResponsePayload;
import edu.manipal.cse.lectureservicereactive.dto.payloads.PayloadHelper;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import edu.manipal.cse.lectureservicereactive.repositories.LectureQuestionRepository;
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
public class StudentResponseController {

    private final StudentResponseService studentResponseService;
    private final LectureQuestionRepository lectureQuestionRepository;

    @QueryMapping
    public Flux<StudentResponse> getResponsesForLectureQuestion(@Argument UUID lectureQuestionId) {
        log.info("GraphQL query: getResponsesForLectureQuestion(id: {})", lectureQuestionId);
        return studentResponseService.findResponsesForLectureQuestion(lectureQuestionId);
    }

    // TODO: @QueryMapping getResponsesByStudent - requires security context

    @MutationMapping
    public Mono<CreateStudentResponsePayload> createStudentResponse(@Argument @Valid CreateStudentResponseInput input) {
        log.info("GraphQL mutation: createStudentResponse(lqId: {}, answerId: {})", input.lectureQuestionId(), input.answerId());

        // TODO: get student response from header
        Mono<UUID> studentIdMono = getCurrentStudentIdFromContext_PLACEHOLDER();

        return studentIdMono
                .switchIfEmpty(Mono.error(new RuntimeException("Authentication required: Student ID not found.")))
                .flatMap(studentId -> studentResponseService.createStudentResponse(input, studentId))
                .map(response -> new CreateStudentResponsePayload(response, Collections.emptyList()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(new CreateStudentResponsePayload(null, PayloadHelper.mapExceptionToUserErrors(e)))
                );
    }

    @SchemaMapping(typeName = "StudentResponse", field = "lectureQuestion")
    public Mono<LectureQuestion> getLectureQuestionForResponse(StudentResponse response, DataLoader<UUID, LectureQuestion> dataLoader) {
        log.debug("Resolving lectureQuestion for responseId: {} using DataLoader", response.getResponseId());
        return Mono.fromFuture(dataLoader.load(response.getLectureQuestionId()));
    }

    @BatchMapping
    public Mono<Map<UUID, LectureQuestion>> loadLectureQuestionsForResponses(List<UUID> lectureQuestionIds) {
        log.debug("Batch loading LectureQuestions for IDs: {}", lectureQuestionIds);
        return lectureQuestionRepository.findAllById(lectureQuestionIds)
                .collectMap(LectureQuestion::getLectureQuestionId);
    }

    private Mono<UUID> getCurrentStudentIdFromContext_PLACEHOLDER() {
        log.warn("!!! Security context retrieval not implemented. Using random UUID !!!");
        return Mono.just(UUID.randomUUID());
    }
}
