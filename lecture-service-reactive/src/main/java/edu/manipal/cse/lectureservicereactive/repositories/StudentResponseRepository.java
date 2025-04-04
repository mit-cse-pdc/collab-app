package edu.manipal.cse.lectureservicereactive.repositories;

import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StudentResponseRepository extends R2dbcRepository<StudentResponse, UUID> {
     Flux<StudentResponse> findByLectureQuestionId(UUID lectureQuestionId);

     Flux<StudentResponse> findByStudentId(UUID studentId);

     Mono<Boolean> existsByStudentIdAndLectureQuestionId(UUID studentId, UUID lectureQuestionId);

     Mono<Long> countByLectureQuestionId(UUID lectureQuestionId);
}
