package edu.manipal.cse.lectureservicereactive.dto.events;

import edu.manipal.cse.lectureservicereactive.models.Lecture;

public record LectureUpdatedEvent(Lecture lecture) implements LectureEvent {}
