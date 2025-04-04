package edu.manipal.cse.lectureservicereactive.services;

import edu.manipal.cse.lectureservicereactive.dto.inputs.studentResponse.CreateStudentResponseInput;
import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StudentResponseService {

    Mono<StudentResponse> findResponseById(UUID responseId);

    Flux<StudentResponse> findResponsesForLectureQuestion(UUID lectureQuestionId);

    Flux<StudentResponse> findResponsesByStudent(UUID studentId);

    Mono<StudentResponse> createStudentResponse(CreateStudentResponseInput input, UUID studentId);

    Mono<Integer> countResponsesForLectureQuestion(UUID lectureQuestionId);
}