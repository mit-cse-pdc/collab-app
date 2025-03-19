package edu.manipal.cse.lectureservice.mappers;

import edu.manipal.cse.lectureservice.dto.CreateLectureInputDto;
import edu.manipal.cse.lectureservice.dto.QuestionDto;
import edu.manipal.cse.lectureservice.models.Lecture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LectureMapper {
    public Lecture toLecture(UUID facultyId, CreateLectureInputDto lectureInput, List<QuestionDto> questions) {
        return null;
    }
}
