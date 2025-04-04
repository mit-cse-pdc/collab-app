package edu.manipal.cse.lectureservicereactive.services;


import edu.manipal.cse.lectureservicereactive.dto.inputs.lecture.*;
import edu.manipal.cse.lectureservicereactive.models.Lecture;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LectureService {

    Mono<Lecture> findLectureById(UUID lectureId);

    Flux<Lecture> findAllLectures(LectureFilterInput filter);

    Mono<Lecture> createLecture(CreateLectureInput input);

    Mono<Lecture> updateLectureStatus(UpdateLectureStatusInput input);

    Mono<Lecture> addQuestionsToLecture(AddQuestionsToLectureInput input);

    Mono<Lecture> removeQuestionsFromLecture(RemoveQuestionsFromLectureInput input);

    Mono<Long> countLectures(LectureFilterInput filter);

    Flux<Lecture> findLectures(LectureFilterInput filter, Pageable pageable);
}