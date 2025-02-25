package edu.manipal.cse.questionbankservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ChapterListResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<ChapterResponse> chapters;
    private int totalChapters;
}