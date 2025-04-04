package edu.manipal.cse.questionbankservice.services.impl;

import edu.manipal.cse.questionbankservice.clients.FacultyClient;
import edu.manipal.cse.questionbankservice.dto.request.*;
import edu.manipal.cse.questionbankservice.dto.response.ApiResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionListResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionResponse;
import edu.manipal.cse.questionbankservice.entities.Answer;
import edu.manipal.cse.questionbankservice.entities.Chapter;
import edu.manipal.cse.questionbankservice.entities.Question;
import edu.manipal.cse.questionbankservice.exceptions.ResourceNotFoundException;
import edu.manipal.cse.questionbankservice.mappers.QuestionMapper;
import edu.manipal.cse.questionbankservice.repositories.ChapterRepository;
import edu.manipal.cse.questionbankservice.repositories.QuestionRepository;
import edu.manipal.cse.questionbankservice.services.AnswerService;
import edu.manipal.cse.questionbankservice.services.QuestionService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private static final String FACULTY_NOT_FOUND = "Faculty not found with ID: %s";
    private static final String CHAPTER_NOT_FOUND = "Chapter not found with ID: %s";
    private static final String QUESTION_NOT_FOUND = "Question not found with ID: %s";

    private final AnswerService answerService;
    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final FacultyClient facultyClient;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "question", key = "#result.questionId")},
            evict = {@CacheEvict(value = "questionList", allEntries = true)}
    )
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        log.debug("Creating question with title: {}", request.getTitle());
        validateFacultyAndChapter(request.getFacultyId(), request.getChapterId());

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(CHAPTER_NOT_FOUND, request.getChapterId())));

        Question question = questionMapper.toEntity(request);
        question.setChapter(chapter);

        // Ensure the bidirectional relationship is maintained
        if (request.getAnswers() != null) {
            for (CreateAnswerRequest answerRequest : request.getAnswers()) {
                Answer answer = new Answer();
                answer.setText(answerRequest.getText());
                answer.setIsCorrect(answerRequest.getIsCorrect());
                answer.setExplanation(answerRequest.getExplanation());
                question.addAnswer(answer); // Use the helper method
            }
        }

        Question savedQuestion = questionRepository.save(question);
        log.info("Created question with ID: {}", savedQuestion.getQuestionId());
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    @Transactional
    @CacheEvict(value = "questionList", allEntries = true)
    public List<QuestionResponse> createBulkQuestions(BulkQuestionRequest request) {
        log.debug("Creating bulk questions for chapter ID: {}", request.getChapterId());
        validateFacultyAndChapter(request.getFacultyId(), request.getChapterId());

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(CHAPTER_NOT_FOUND, request.getChapterId())));

        List<Question> questions = request.getQuestions().stream()
                .map(questionMapper::toEntity)
                .peek(question -> {
                    question.setFacultyId(request.getFacultyId());
                    question.setChapter(chapter);
                })
                .collect(Collectors.toList());

        List<Question> savedQuestions = questionRepository.saveAll(questions);

        log.info("Created {} questions for chapter ID: {}", savedQuestions.size(), request.getChapterId());
        return savedQuestions.stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "questionList", key = "#root.methodName")
    public QuestionListResponse getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return questionMapper.toListResponse(questions);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "question", key = "#questionId", unless = "#result == null")
    public QuestionResponse getQuestion(UUID questionId) {
        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(QUESTION_NOT_FOUND, questionId)));

        return questionMapper.toResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "questionsByChapter", key = "#chapterId")
    public QuestionListResponse getQuestionsByChapter(UUID chapterId) {
        List<Question> questions = questionRepository.findByChapter_ChapterId(chapterId);
        return questionMapper.toListResponse(questions);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "questionsByFaculty", key = "#facultyId")
    public QuestionListResponse getQuestionsByFaculty(UUID facultyId) {
        List<Question> questions = questionRepository.findByFacultyId(facultyId);
        return questionMapper.toListResponse(questions);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "questionsByFacultyAndChapter", key = "#facultyId + '_' + #chapterId")
    public QuestionListResponse getQuestionsByFacultyAndChapter(UUID facultyId, UUID chapterId) {
        List<Question> questions = questionRepository.findByFacultyIdAndChapter_ChapterId(facultyId, chapterId);
        return questionMapper.toListResponse(questions);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "searchQuestions", key = "#request.toString()")
    public QuestionListResponse searchQuestions(QuestionSearchRequest request) {
        Specification<Question> spec = buildSearchSpecification(request);
        List<Question> questions = questionRepository.findAll(spec);
        return questionMapper.toListResponse(questions);
    }

    private Specification<Question> buildSearchSpecification(QuestionSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addChapterPredicate(predicates, root, criteriaBuilder, request);
            addFacultyPredicate(predicates, root, criteriaBuilder, request);
            addQuestionTypePredicate(predicates, root, criteriaBuilder, request);
            addSearchTermPredicate(predicates, root, criteriaBuilder, request);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addChapterPredicate(List<Predicate> predicates, Root<Question> root, CriteriaBuilder criteriaBuilder, QuestionSearchRequest request) {
        if (request.getChapterId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("chapter").get("chapterId"), request.getChapterId()));
        }
    }

    private void addFacultyPredicate(List<Predicate> predicates, Root<Question> root, CriteriaBuilder criteriaBuilder, QuestionSearchRequest request) {
        if (request.getFacultyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("facultyId"), request.getFacultyId()));
        }
    }

    private void addQuestionTypePredicate(List<Predicate> predicates, Root<Question> root, CriteriaBuilder criteriaBuilder, QuestionSearchRequest request) {
        if (request.getQuestionType() != null) {
            predicates.add(criteriaBuilder.equal(root.get("questionType"), request.getQuestionType()));
        }
    }

    private void addSearchTermPredicate(List<Predicate> predicates, Root<Question> root, CriteriaBuilder criteriaBuilder, QuestionSearchRequest request) {
        if (request.getSearchTerm() != null && !request.getSearchTerm().isEmpty()) {
            String searchTerm = "%" + request.getSearchTerm().toLowerCase() + "%";
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("text")), searchTerm)
            ));
        }
    }

    @Override
    @Transactional
    @Caching(
            put = {@CachePut(value = "question", key = "#questionId")},
            evict = {@CacheEvict(value = "questionList", allEntries = true)}
    )
    public QuestionResponse updateQuestion(UUID questionId, UpdateQuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(QUESTION_NOT_FOUND, questionId)));

        questionMapper.updateEntity(request, question);
        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "question", key = "#questionId"),
                    @CacheEvict(value = "questionList", allEntries = true)
            }
    )
    public boolean deleteQuestion(UUID questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException(String.format(QUESTION_NOT_FOUND, questionId));
        }
        questionRepository.deleteById(questionId);
        return true;
    }

    @Override
    @Cacheable(value = "questionOwnership", key = "#questionId + '_' + #facultyId")
    public boolean checkQuestionOwnershipByFaculty(UUID questionId, UUID facultyId) {
        return questionRepository.existsByQuestionIdAndFacultyId(questionId, facultyId);
    }

    @Override
    @Cacheable(value = "validateQuestions", key = "#chapterId + '_' + #questionIds.toString()")
    public boolean validateQuestionsInChapter(UUID chapterId, List<UUID> questionIds) {
        long count = questionRepository.countByChapter_ChapterIdAndQuestionIdIn(chapterId, questionIds);
        return count == questionIds.size();
    }

    @Override
    public List<QuestionResponse> validateAllQuestions(List<UUID> uuids) {
        return questionRepository.findByQuestionIdIn(uuids)
                .stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateFacultyAndChapter(UUID facultyId, UUID chapterId) {
        validateFaculty(facultyId);
        validateChapter(chapterId);
    }

    private void validateFaculty(UUID facultyId) {
        ResponseEntity<ApiResponse<Boolean>> response = facultyClient.facultyExistsById(facultyId);
        if (response == null || !Boolean.TRUE.equals(response.getBody().getData())) {
            throw new ResourceNotFoundException(String.format(FACULTY_NOT_FOUND, facultyId));
        }
    }

    private void validateChapter(UUID chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new ResourceNotFoundException(String.format(CHAPTER_NOT_FOUND, chapterId));
        }
    }
}