package edu.manipal.cse.lectureservicereactive.dto.outputs;

import edu.manipal.cse.lectureservicereactive.models.Lecture;
import java.util.List;

public record LecturePage(
        List<Lecture> content,
        int totalPages,
        long totalElements,
        int currentPage,
        int pageSize
) {}
