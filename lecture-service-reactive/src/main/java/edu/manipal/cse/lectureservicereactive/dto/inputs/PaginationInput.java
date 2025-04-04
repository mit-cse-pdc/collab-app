package edu.manipal.cse.lectureservicereactive.dto.inputs;

import jakarta.validation.constraints.Min;

public record PaginationInput(
        @Min(value = 0)
        int page,

        @Min(value = 1)
        int size
) {
    public PaginationInput() {
        this(0, 10);
    }
}
