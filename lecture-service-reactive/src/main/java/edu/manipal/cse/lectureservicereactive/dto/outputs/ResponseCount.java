package edu.manipal.cse.lectureservicereactive.dto.outputs;

import java.util.UUID;

public record ResponseCount(
        UUID lectureQuestionId,
        int count
) {}
