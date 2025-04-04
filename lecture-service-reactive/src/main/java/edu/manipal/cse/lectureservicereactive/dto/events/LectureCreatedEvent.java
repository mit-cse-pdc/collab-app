package edu.manipal.cse.lectureservicereactive.dto.events;

import edu.manipal.cse.lectureservicereactive.models.Lecture;

public record LectureCreatedEvent(Lecture lecture) implements LectureEvent {}
