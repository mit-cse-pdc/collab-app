package edu.manipal.cse.lectureservicereactive.controllers;

import edu.manipal.cse.lectureservicereactive.dto.inputs.PaginationInput;
import edu.manipal.cse.lectureservicereactive.dto.inputs.lecture.*;
import edu.manipal.cse.lectureservicereactive.dto.outputs.LecturePage;
import edu.manipal.cse.lectureservicereactive.dto.payloads.CreateLecturePayload;
import edu.manipal.cse.lectureservicereactive.dto.payloads.PayloadHelper;
import edu.manipal.cse.lectureservicereactive.dto.payloads.UpdateLecturePayload;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.services.LectureQuestionService;
import edu.manipal.cse.lectureservicereactive.services.LectureService;
import graphql.kickstart.servlet.context.GraphQLServletContext;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LectureController {

    private final LectureService lectureService;
    private final LectureQuestionService lectureQuestionService;

    @QueryMapping
    public Mono<Lecture> getLectureById(@Argument UUID lectureId) {
        log.info("GraphQL query: getLectureById(lectureId: {})", lectureId);
        return lectureService.findLectureById(lectureId);
    }

    @QueryMapping
    public Mono<LecturePage> getLectures(@Argument LectureFilterInput filter, @Argument PaginationInput pagination, DataFetchingEnvironment environment) {
        log.info("GraphQL query: getLectures(filter: {}, pagination: {})", filter, pagination);

        GraphQLServletContext context = environment.getContext();
        HttpServletRequest servletRequest = (HttpServletRequest) context.getHttpServletRequest();
        String userId = null;
//        HttpServletRequest servletRequest = null;
        if (servletRequest != null) {
            userId = servletRequest.getHeader("X-User-Id");
            log.debug("User ID from request header: {}", userId);
        } else {
            log.warn("No HTTP request found in GraphQL context");
            // Handle missing user ID case - perhaps return an error or use a default
        }

        final PaginationInput effectivePagination = (pagination == null)
                ? new PaginationInput() // Uses default constructor (page=0, size=10)
                : pagination;

        Pageable pageable = PageRequest.of(effectivePagination.page(), effectivePagination.size());

        Mono<List<Lecture>> pageContentMono = lectureService.findLectures(filter, pageable)
                .collectList();

        Mono<Long> totalCountMono = lectureService.countLectures(filter);

        return Mono.zip(pageContentMono, totalCountMono)
                .map(tuple -> {
                    List<Lecture> content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int pageSize = pageable.getPageSize();
                    int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;

                    return new LecturePage(content, totalPages, totalElements, pageable.getPageNumber(), pageSize);
                });
    }


    @MutationMapping
    public Mono<CreateLecturePayload> createLecture(@Argument @Valid CreateLectureInput input) {
        log.info("GraphQL mutation: createLecture(title: {})", input.title());
        return lectureService.createLecture(input)
                .map(lecture -> new CreateLecturePayload(lecture, Collections.emptyList()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(new CreateLecturePayload(null, PayloadHelper.mapExceptionToUserErrors(e)))
                );
    }

    @MutationMapping
    public Mono<UpdateLecturePayload> updateLectureStatus(@Argument @Valid UpdateLectureStatusInput input) {
        log.info("GraphQL mutation: updateLectureStatus(id: {}, status: {})", input.lectureId(), input.status());
        return lectureService.updateLectureStatus(input)
                .map(lecture -> new UpdateLecturePayload(lecture, Collections.emptyList()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(new UpdateLecturePayload(null, PayloadHelper.mapExceptionToUserErrors(e)))
                );
    }

    @MutationMapping
    public Mono<UpdateLecturePayload> addQuestionsToLecture(@Argument @Valid AddQuestionsToLectureInput input) {
        log.info("GraphQL mutation: addQuestionsToLecture(lectureId: {})", input.lectureId());
        return lectureService.addQuestionsToLecture(input)
                .map(lecture -> new UpdateLecturePayload(lecture, Collections.emptyList()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(new UpdateLecturePayload(null, PayloadHelper.mapExceptionToUserErrors(e)))
                );
    }

    @MutationMapping
    public Mono<UpdateLecturePayload> removeQuestionsFromLecture(@Argument @Valid RemoveQuestionsFromLectureInput input) {
        log.info("GraphQL mutation: removeQuestionsFromLecture(lectureId: {})", input.lectureId());
        return lectureService.removeQuestionsFromLecture(input)
                .map(lecture -> new UpdateLecturePayload(lecture, Collections.emptyList()))
                .onErrorResume(Exception.class, e ->
                        Mono.just(new UpdateLecturePayload(null, PayloadHelper.mapExceptionToUserErrors(e)))
                );
    }

    @SchemaMapping(typeName = "Lecture", field = "lectureQuestions")
    public Flux<LectureQuestion> getLectureQuestions(Lecture lecture) {
        log.debug("Resolving lectureQuestions for lectureId: {}", lecture.getLectureId());
        return lectureQuestionService.findQuestionsForLecture(lecture.getLectureId());
    }
}