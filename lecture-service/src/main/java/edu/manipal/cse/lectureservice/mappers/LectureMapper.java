package edu.manipal.cse.lectureservice.mappers;

import edu.manipal.cse.lectureservice.dto.CreateLectureInputDto;
import edu.manipal.cse.lectureservice.dto.QuestionDto;
import edu.manipal.cse.lectureservice.models.Lecture;
import edu.manipal.cse.lectureservice.models.LectureQuestion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LectureMapper {

    public Lecture toLecture(UUID facultyId, CreateLectureInputDto lectureInput, List<QuestionDto> questions) {
        Lecture lecture = new Lecture();
        lecture.setLectureId(UUID.randomUUID());
        lecture.setFacultyId(facultyId);
        lecture.setChapterId(lectureInput.chapterId());
        lecture.setTitle(lectureInput.title());
        lecture.setStatus(Lecture.LectureStatus.SCHEDULED);

        List<LectureQuestion> lectureQuestions = questions.stream()
                .map(question -> {
                    LectureQuestion lectureQuestion = new LectureQuestion();
                    lectureQuestion.setQuestionId(question.getQuestionId());
                    lectureQuestion.setStatus(LectureQuestion.LectureQuestionStatus.PENDING);
                    lectureQuestion.setLecture(lecture); // Set bidirectional relationship
                    return lectureQuestion;
                })
                .collect(Collectors.toList());

        lecture.setLectureQuestions(lectureQuestions);

        return lecture;
    }
}