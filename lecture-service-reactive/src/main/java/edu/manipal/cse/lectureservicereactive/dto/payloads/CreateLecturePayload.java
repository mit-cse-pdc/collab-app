package edu.manipal.cse.lectureservicereactive.dto.payloads;

import edu.manipal.cse.lectureservicereactive.models.Lecture;
import java.util.List;

public record CreateLecturePayload(Lecture lecture, List<UserError> userErrors) {}
