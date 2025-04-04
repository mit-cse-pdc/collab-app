package edu.manipal.cse.lectureservicereactive.repositories;

import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface LectureQuestionRepository extends R2dbcRepository<LectureQuestion, UUID> {
    Flux<LectureQuestion> findByLectureId(UUID lectureId);
}
