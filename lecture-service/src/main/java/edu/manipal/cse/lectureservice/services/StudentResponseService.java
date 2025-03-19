package edu.manipal.cse.lectureservice.services;

import edu.manipal.cse.lectureservice.dto.CreateStudentResponseDto;
import edu.manipal.cse.lectureservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservice.models.Lecture;
import edu.manipal.cse.lectureservice.models.LectureQuestion;
import edu.manipal.cse.lectureservice.models.StudentResponse;
import edu.manipal.cse.lectureservice.repositories.LectureRepository;
import edu.manipal.cse.lectureservice.repositories.StudentResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentResponseService {
    private final StudentResponseRepository studentResponseRepository;
    private final LectureRepository lectureRepository;

    public StudentResponse createStudentResponse(CreateStudentResponseDto studentResponseDto, UUID studentId) throws ResourceNotFoundException {
        LectureQuestion lectureQuestion = lectureRepository
                .findById(studentResponseDto.lectureId())
                .map(Lecture::getLectureQuestions)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + studentResponseDto.lectureId()))
                .stream()
                .filter(l -> l.getLectureQuestionId().equals(studentResponseDto.questionId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + studentResponseDto.questionId()));


        StudentResponse studentResponse = StudentResponse.builder()
                .studentId(studentId)
                .answerId(studentResponseDto.optionId())
                .lectureQuestion(lectureQuestion)
                .build();

        return studentResponseRepository.save(studentResponse);
    }
}
