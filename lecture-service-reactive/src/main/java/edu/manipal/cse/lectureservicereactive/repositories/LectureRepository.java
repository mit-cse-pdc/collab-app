package edu.manipal.cse.lectureservicereactive.repositories;

import edu.manipal.cse.lectureservicereactive.models.Lecture;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface LectureRepository extends R2dbcRepository<Lecture, UUID> {
    Flux<Lecture> findByStatus(Lecture.LectureStatus status);
}
