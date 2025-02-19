package com.pdc.questionbankservice.mappers;

import com.pdc.questionbankservice.dto.request.CreateChapterRequest;
import com.pdc.questionbankservice.dto.request.UpdateChapterRequest;
import com.pdc.questionbankservice.dto.response.ChapterResponse;
import com.pdc.questionbankservice.entities.Chapter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChapterMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ChapterMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Chapter toEntity(CreateChapterRequest request) {
        Chapter chapter = modelMapper.map(request, Chapter.class);
        chapter.setCreatedAt(LocalDateTime.now());
        chapter.setUpdatedAt(LocalDateTime.now());
        return chapter;
    }

    public ChapterResponse toResponse(Chapter chapter) {
        ChapterResponse response = modelMapper.map(chapter, ChapterResponse.class);

        // Since questionCount isn't directly in Chapter, you might need to calculate it:
        response.setQuestionCount(chapter.getQuestions() != null ? chapter.getQuestions().size() : 0);
        return response;
    }

    public void updateEntity(UpdateChapterRequest request, Chapter chapter) {
        modelMapper.map(request, chapter);
        chapter.setUpdatedAt(LocalDateTime.now());
    }
}