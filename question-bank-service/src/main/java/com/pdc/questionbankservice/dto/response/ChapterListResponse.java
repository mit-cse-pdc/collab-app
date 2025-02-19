package com.pdc.questionbankservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChapterListResponse {
    private List<ChapterResponse> chapters;
    private int totalChapters;
}