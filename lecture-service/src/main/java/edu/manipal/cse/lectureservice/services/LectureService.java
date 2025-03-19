package edu.manipal.cse.lectureservice.services;

import edu.manipal.cse.lectureservice.clients.QuestionClient;
import edu.manipal.cse.lectureservice.dto.CreateLectureInputDto;
import edu.manipal.cse.lectureservice.dto.QuestionDto;
import edu.manipal.cse.lectureservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservice.mappers.LectureMapper;
import edu.manipal.cse.lectureservice.models.Lecture;
import edu.manipal.cse.lectureservice.repositories.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final QuestionClient questionClient;
    private final LectureMapper lectureMapper;

    public List<Lecture> getAllLectures() {
        return lectureRepository.findAll();
    }

    public Lecture createLecture(CreateLectureInputDto lectureInput, UUID facultyId) {
        List<QuestionDto> questions = questionClient.validateAllQuestions(lectureInput.lectureQuestions());
        Lecture lecture = lectureMapper.toLecture(facultyId, lectureInput, questions);
        return lectureRepository.save(lecture);
    }

    public Lecture updateLectureStatus(UUID lectureId, Lecture.LectureStatus status) throws ResourceNotFoundException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id+" + lectureId));
        lecture.setStatus(status);
        return lectureRepository.save(lecture);
    }

    public Flux<List<Lecture>> getLectures() {
        List<Lecture> lectures = lectureRepository.findAll();
        return Flux.fromStream(Stream.of(lectures));
    }

    public Flux<Lecture> getLecture(UUID lectureId) throws ResourceNotFoundException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id" + lectureId));
        return Flux.just(lecture);
    }
}
