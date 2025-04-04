package edu.manipal.cse.lectureservice.handlers;

import edu.manipal.cse.lectureservice.exceptions.LectureNotFoundException;
import graphql.GraphQLError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public void handleLectureNotFound(LectureNotFoundException ex) {
        GraphQLError.newError()
                .message(ex.getMessage())
                .build();
    }
}
