package edu.manipal.cse.lectureservicereactive.dto.inputs.lectureQuestion;

import edu.manipal.cse.lectureservicereactive.models.LectureQuestion;

public record LectureQuestionFilterInput(
        LectureQuestion.LectureQuestionStatus status
) {}
