package edu.manipal.cse.lectureservicereactive.controllers;

import edu.manipal.cse.lectureservicereactive.dto.events.LectureEvent;
import edu.manipal.cse.lectureservicereactive.dto.outputs.ResponseCount;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import edu.manipal.cse.lectureservicereactive.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @SubscriptionMapping
    public Flux<LectureEvent> lectureActivity() {
        log.info("GraphQL subscription request: lectureActivity");
        return subscriptionService.lectureActivityStream();
    }

    @SubscriptionMapping
    public Flux<Lecture> lectureUpdated(@Argument UUID lectureId) {
        log.info("GraphQL subscription request: lectureUpdated(lectureId: {})", lectureId);
        return subscriptionService.lectureUpdateStream(lectureId);
    }

    @SubscriptionMapping
    public Flux<ResponseCount> studentResponseCountUpdated(@Argument UUID lectureQuestionId) {
        log.info("GraphQL subscription request: studentResponseCountUpdated(lectureQuestionId: {})", lectureQuestionId);
        return subscriptionService.responseCountStream(lectureQuestionId);
    }

    @SubscriptionMapping
    public Flux<LectureQuestion> lectureQuestionStatusChanged(@Argument UUID lectureQuestionId) {
        log.info("GraphQL subscription request: lectureQuestionStatusChanged(lectureQuestionId: {})", lectureQuestionId);
        return subscriptionService.lectureQuestionUpdateStream(lectureQuestionId);
    }

    @SubscriptionMapping
    public Flux<StudentResponse> studentResponded(@Argument UUID lectureQuestionId) {
        log.info("GraphQL subscription request: studentResponded(lectureQuestionId: {})", lectureQuestionId);
        return subscriptionService.studentResponseStream(lectureQuestionId);
    }
}
