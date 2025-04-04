package edu.manipal.cse.lectureservicereactive.services.impl;

import edu.manipal.cse.lectureservicereactive.dto.events.LectureEvent;
import edu.manipal.cse.lectureservicereactive.dto.outputs.ResponseCount;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion; // Import LectureQuestion
import edu.manipal.cse.lectureservicereactive.models.StudentResponse; // Import StudentResponse
import edu.manipal.cse.lectureservicereactive.services.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final Sinks.Many<LectureEvent> lectureActivitySink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Lecture> lectureUpdateSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<ResponseCount> responseCountSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<LectureQuestion> lectureQuestionUpdateSink = Sinks.many().multicast().onBackpressureBuffer(); // Sink for LQ updates
    private final Sinks.Many<StudentResponse> studentResponseSink = Sinks.many().multicast().onBackpressureBuffer(); // Sink for SR creations

    @Override
    public void publishLectureEvent(LectureEvent event) {
        log.debug("Publishing LectureEvent: {}", event.getClass().getSimpleName());
        lectureActivitySink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Override
    public void publishLectureUpdate(Lecture lecture) {
        log.debug("Publishing Lecture update for ID: {}", lecture.getLectureId());
        lectureUpdateSink.emitNext(lecture, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Override
    public void publishResponseCountUpdate(ResponseCount responseCount) {
        log.debug("Publishing ResponseCount update: {}", responseCount);
        responseCountSink.emitNext(responseCount, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Override
    public void publishLectureQuestionUpdate(LectureQuestion lectureQuestion) {
        if (lectureQuestion == null) {
            log.warn("Attempted to publish null LectureQuestion update");
            return;
        }
        log.debug("Publishing LectureQuestion update for ID: {}", lectureQuestion.getLectureQuestionId());
        Sinks.EmitResult result = lectureQuestionUpdateSink.tryEmitNext(lectureQuestion);
        if (result.isFailure()) {
            log.warn("Failed to emit LectureQuestion update for ID {}: {}", lectureQuestion.getLectureQuestionId(), result);
        }
    }

    @Override
    public void publishStudentResponseCreated(StudentResponse response) {
        if (response == null) {
            log.warn("Attempted to publish null StudentResponse creation");
            return;
        }
        log.debug("Publishing StudentResponse creation for ID: {}", response.getResponseId());
        studentResponseSink.emitNext(response, Sinks.EmitFailureHandler.FAIL_FAST);
    }


    // --- Streaming Methods ---
    @Override
    public Flux<LectureEvent> lectureActivityStream() {
        log.info("New subscriber for lectureActivityStream");
        return lectureActivitySink.asFlux();
    }

    @Override
    public Flux<Lecture> lectureUpdateStream(UUID lectureId) {
        log.info("New subscriber for lectureUpdateStream (lectureId: {})", lectureId);
        return lectureUpdateSink.asFlux()
                .filter(lecture -> lecture != null && lecture.getLectureId().equals(lectureId));
    }

    @Override
    public Flux<ResponseCount> responseCountStream(UUID lectureQuestionId) {
        log.info("New subscriber for responseCountStream (lectureQuestionId: {})", lectureQuestionId);
        return responseCountSink.asFlux()
                .filter(count -> count != null && count.lectureQuestionId().equals(lectureQuestionId));
    }

    @Override
    public Flux<LectureQuestion> lectureQuestionUpdateStream(UUID lectureQuestionId) {
        log.info("New subscriber for lectureQuestionUpdateStream (lectureQuestionId: {})", lectureQuestionId);
        return lectureQuestionUpdateSink.asFlux()
                .filter(lq -> lq != null && lq.getLectureQuestionId().equals(lectureQuestionId));
    }

    @Override
    public Flux<StudentResponse> studentResponseStream(UUID lectureQuestionId) {
        log.info("New subscriber for studentResponseStream (lectureQuestionId: {})", lectureQuestionId);
        return studentResponseSink.asFlux()
                .filter(response -> response != null && response.getLectureQuestionId().equals(lectureQuestionId));
    }
}
