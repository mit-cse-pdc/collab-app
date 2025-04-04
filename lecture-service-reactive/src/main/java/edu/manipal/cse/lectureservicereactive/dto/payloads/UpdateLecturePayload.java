package edu.manipal.cse.lectureservicereactive.dto.payloads;

import edu.manipal.cse.lectureservicereactive.models.Lecture;
import java.util.List;

public record UpdateLecturePayload(Lecture lecture, List<UserError> userErrors) {}
