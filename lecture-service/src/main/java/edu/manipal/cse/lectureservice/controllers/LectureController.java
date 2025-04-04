package edu.manipal.cse.lectureservice.controllers;


import edu.manipal.cse.lectureservice.dto.CreateLectureInputDto;
import edu.manipal.cse.lectureservice.dto.CreateStudentResponseDto;
import edu.manipal.cse.lectureservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservice.models.Lecture;
import edu.manipal.cse.lectureservice.models.LectureQuestion;
import edu.manipal.cse.lectureservice.models.LectureQuestion.LectureQuestionStatus;
import edu.manipal.cse.lectureservice.models.StudentResponse;
import edu.manipal.cse.lectureservice.services.LectureQuestionService;
import edu.manipal.cse.lectureservice.services.LectureService;
import edu.manipal.cse.lectureservice.services.StudentResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;
    private final LectureQuestionService lectureQuestionService;
    private final StudentResponseService studentResponseService;


    // Sinks for reactive updates
    private final Sinks.Many<List<Lecture>> lecturesSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Lecture> lectureDetailsSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<LectureQuestion> lectureQuestionSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<StudentResponse> studentResponseSink = Sinks.many().multicast().onBackpressureBuffer();


    @QueryMapping
    public List<Lecture> getAllLectures() {
        return lectureService.getAllLectures();
    }

    @SubscriptionMapping
    public Flux<List<Lecture>> getLectures() {
        return lecturesSink.asFlux().startWith(lectureService.getAllLectures());
    }

    @SubscriptionMapping
    public Flux<Lecture> getLectureDetails(@Argument UUID lectureId) {
        try {
            return lectureDetailsSink.asFlux()
                    .filter(lecture -> lecture.getLectureId().equals(lectureId))
                    .startWith(lectureService.getLecture(lectureId));
        } catch (IllegalArgumentException e) {
            return Flux.error(new IllegalArgumentException("Invalid UUID format for lectureId", e));
        }
    }

    @MutationMapping
    public Lecture createLecture(@Argument CreateLectureInputDto lectureInput) {
        Lecture createdLecture = lectureService.createLecture(lectureInput);
        lecturesSink.tryEmitNext(lectureService.getAllLectures());
        lectureDetailsSink.tryEmitNext(createdLecture);
        return createdLecture;
    }


    @MutationMapping
    public Lecture updateLectureStatus(@Argument UUID lectureId, @Argument Lecture.LectureStatus status) throws ResourceNotFoundException {
        try {
            Lecture updatedLecture = lectureService.updateLectureStatus(lectureId, status, UUID.randomUUID()); // Using random UUID
            lectureDetailsSink.tryEmitNext(updatedLecture);
            lecturesSink.tryEmitNext(lectureService.getAllLectures());
            return updatedLecture;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for lectureId", e);
        }
    }


    @MutationMapping
    public LectureQuestion updateLectureQuestionStatus(@Argument UUID lectureQuestionId, @Argument LectureQuestionStatus status) throws ResourceNotFoundException {
        try {
            LectureQuestion updatedQuestion = lectureQuestionService.updateLectureQuestionStatus(lectureQuestionId, status);
            lectureQuestionSink.tryEmitNext(updatedQuestion);
            return updatedQuestion;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for lectureQuestionId", e);
        }
    }


    @MutationMapping
    public StudentResponse createStudentResponse(@Argument CreateStudentResponseDto studentResponse) throws ResourceNotFoundException {
        StudentResponse createdResponse = studentResponseService.createStudentResponse(studentResponse, UUID.randomUUID()); // Random student UUID
        studentResponseSink.tryEmitNext(createdResponse);
        return createdResponse;
    }
}
