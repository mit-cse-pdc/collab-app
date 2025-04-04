package edu.manipal.cse.lectureservicereactive.dto.payloads;

import edu.manipal.cse.lectureservicereactive.models.StudentResponse;
import java.util.List;

public record CreateStudentResponsePayload(StudentResponse studentResponse, List<UserError> userErrors) {}
