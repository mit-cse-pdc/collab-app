package edu.manipal.cse.lectureservicereactive.services;


import edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion.UpdateLectureQuestionStatusInput;
import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LectureQuestionService {

    Mono<LectureQuestion> findLectureQuestionById(UUID lectureQuestionId);

    Flux<LectureQuestion> findQuestionsForLecture(UUID lectureId);

    Mono<LectureQuestion> updateStatus(UpdateLectureQuestionStatusInput input);
}