package edu.manipal.cse.lectureservicereactive.dto.events;

import java.util.UUID;

public record LectureDeletedEvent(UUID lectureId) implements LectureEvent {}
