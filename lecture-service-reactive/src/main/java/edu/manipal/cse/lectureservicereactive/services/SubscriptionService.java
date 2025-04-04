package edu.manipal.cse.lectureservicereactive.services;

import edu.manipal.cse.lectureservicereactive.dto.events.LectureEvent;
import edu.manipal.cse.lectureservicereactive.dto.outputs.ResponseCount;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SubscriptionService {

    void publishLectureEvent(LectureEvent event);
    void publishLectureUpdate(Lecture lecture);
    void publishLectureQuestionUpdate(LectureQuestion lectureQuestion);
    void publishResponseCountUpdate(ResponseCount responseCount);
    void publishStudentResponseCreated(StudentResponse response);

    Flux<LectureEvent> lectureActivityStream();
    Flux<Lecture> lectureUpdateStream(UUID lectureId);
    Flux<LectureQuestion> lectureQuestionUpdateStream(UUID lectureQuestionId);
    Flux<ResponseCount> responseCountStream(UUID lectureQuestionId);
    Flux<StudentResponse> studentResponseStream(UUID lectureQuestionId);
}
