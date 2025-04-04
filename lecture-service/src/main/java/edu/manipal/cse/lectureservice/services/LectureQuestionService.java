package edu.manipal.cse.lectureservice.services;

import edu.manipal.cse.lectureservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservice.models.LectureQuestion;
import edu.manipal.cse.lectureservice.repositories.LectureQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LectureQuestionService {
    private final LectureQuestionRepository lectureQuestionRepository;

    public LectureQuestion updateLectureQuestionStatus(UUID lectureQuestionId, LectureQuestion.LectureQuestionStatus newStatus) throws ResourceNotFoundException {
        return lectureQuestionRepository.findById(lectureQuestionId)
                .map(lectureQuestion -> {
                    lectureQuestion.setStatus(newStatus);
                    return lectureQuestionRepository.save(lectureQuestion);
                })
                .orElseThrow(() -> new ResourceNotFoundException("LectureQuestion not found with id: " + lectureQuestionId));
    }
}
