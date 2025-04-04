package edu.manipal.cse.lectureservice.services;

import edu.manipal.cse.lectureservice.clients.QuestionClient;
import edu.manipal.cse.lectureservice.dto.ApiResponse;
import edu.manipal.cse.lectureservice.dto.CreateLectureInputDto;
import edu.manipal.cse.lectureservice.dto.CreateLectureQuestionInputDto;
import edu.manipal.cse.lectureservice.dto.QuestionDto;
import edu.manipal.cse.lectureservice.exceptions.LectureCreationException;
import edu.manipal.cse.lectureservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.lectureservice.models.Lecture;
import edu.manipal.cse.lectureservice.models.LectureQuestion;
import edu.manipal.cse.lectureservice.repositories.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Applies to all public methods unless overridden
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final QuestionClient questionClient;

    public List<Lecture> getAllLectures() {
        return lectureRepository.findAllWithQuestionsFetched();
    }

    @Transactional
    public Lecture createLecture(CreateLectureInputDto lectureInput) {
        validateLectureInput(lectureInput);
        log.info("Creating lecture for faculty: {}", lectureInput.facultyId());

        List<QuestionDto> validatedQuestions = validateQuestions(lectureInput.lectureQuestions());

        Lecture lecture = new Lecture();
        lecture.setFacultyId(lectureInput.facultyId());
        lecture.setChapterId(lectureInput.chapterId());
        lecture.setTitle(lectureInput.title());
        lecture.setStatus(Lecture.LectureStatus.SCHEDULED);

        List<LectureQuestion> lectureQuestions = createLectureQuestions(lecture, validatedQuestions);
        lecture.setLectureQuestions(lectureQuestions);

        try {
            Lecture savedLecture = lectureRepository.save(lecture);
            log.info("Lecture created successfully with ID: {}", savedLecture.getLectureId());
            return savedLecture;
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create lecture due to data integrity violation", e);
            throw new LectureCreationException("Failed to create lecture due to data integrity issues", e);
        }
    }

    private void validateLectureInput(CreateLectureInputDto lectureInput) {
        if (lectureInput == null) {
            throw new IllegalArgumentException("Lecture input cannot be null");
        }
        if (lectureInput.facultyId() == null) {
            throw new IllegalArgumentException("Faculty ID is required");
        }
        if (lectureInput.chapterId() == null) {
            throw new IllegalArgumentException("Chapter ID is required");
        }
        if (StringUtils.isBlank(lectureInput.title())) {
            throw new IllegalArgumentException("Lecture title cannot be empty");
        }
    }

    private List<QuestionDto> validateQuestions(List<CreateLectureQuestionInputDto> questionInputs) {
        if (questionInputs == null || questionInputs.isEmpty()) {
            log.info("No questions provided for lecture creation.");
            return Collections.emptyList();
        }

        List<UUID> questionUUIDs = questionInputs.stream()
                .map(CreateLectureQuestionInputDto::questionId)
                .toList();
        log.info("Attempting to validate questions: {}", questionUUIDs);

        try {
            ResponseEntity<ApiResponse<List<QuestionDto>>> response = questionClient.validateAllQuestions(questionUUIDs);

            if (response == null || response.getBody() == null || !response.getBody().isSuccess()) {
                log.error("Failed response received from question validation. Status: {}, Body: {}",
                        response != null ? response.getStatusCode() : "N/A",
                        response != null ? response.getBody() : "N/A");
                throw new RuntimeException("Failed to validate questions via question-service.");
            }

            List<QuestionDto> validatedQuestions = response.getBody().getData();
            if (validatedQuestions == null) {
                log.warn("Validated questions list was null in the response body.");
                throw new RuntimeException("Received null data list from question validation service.");
            }

            if (validatedQuestions.size() != questionUUIDs.size()) {
                log.warn("Mismatch in validated questions count. Requested: {}, Validated: {}. Proceeding with validated ones.",
                        questionUUIDs.size(), validatedQuestions.size());
            }

            log.info("Successfully validated {} questions.", validatedQuestions.size());
            return validatedQuestions;

        } catch (Exception e) {
            log.error("Error occurred during question validation call to question-service", e);
            throw new LectureCreationException("Error communicating with question-service for validation.", e);
        }
    }

    private List<LectureQuestion> createLectureQuestions(Lecture lecture, List<QuestionDto> validatedQuestions) {
        return validatedQuestions.stream()
                .map(questionDto -> {
                    LectureQuestion lectureQuestion = new LectureQuestion();
                    lectureQuestion.setLecture(lecture);
                    lectureQuestion.setQuestionId(questionDto.getQuestionId());
                    lectureQuestion.setStatus(LectureQuestion.LectureQuestionStatus.PENDING);
                    return lectureQuestion;
                })
                .collect(Collectors.toList());
    }

    public Lecture updateLectureStatus(UUID lectureId, Lecture.LectureStatus status, UUID facultyId) throws ResourceNotFoundException {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId));

        if (!lecture.getFacultyId().equals(facultyId)) {
            throw new ResourceNotFoundException("Faculty " + facultyId + " does not own lecture " + lectureId);
        }

        lecture.setStatus(status);
        return lectureRepository.save(lecture);
    }

    public Flux<List<Lecture>> getLectures() {
        return Flux.just(lectureRepository.findAll());
    }

    public Mono<Lecture> getLecture(UUID lectureId) {
        return Mono.fromCallable(() -> lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId)));
    }
}