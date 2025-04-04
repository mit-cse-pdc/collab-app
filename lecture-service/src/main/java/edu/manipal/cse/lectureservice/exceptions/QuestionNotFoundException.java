package edu.manipal.cse.lectureservice.exceptions;

import java.util.UUID;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(UUID uuid) {
        super("Question not found with id: " + uuid.toString());
    }
}
