package edu.manipal.cse.lectureservice.exceptions;

public class LectureNotFoundException extends RuntimeException {
    public LectureNotFoundException(String message) {
        super(message);
    }
}
