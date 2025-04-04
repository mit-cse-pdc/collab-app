package edu.manipal.cse.lectureservicereactive.dto.payloads;

import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;
import java.util.List;

public record UpdateLectureQuestionPayload(LectureQuestion lectureQuestion, List<UserError> userErrors) {}
