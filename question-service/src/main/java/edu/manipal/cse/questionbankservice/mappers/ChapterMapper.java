package edu.manipal.cse.questionbankservice.mappers;

import edu.manipal.cse.questionbankservice.dto.request.CreateChapterRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateChapterRequest;
import edu.manipal.cse.questionbankservice.dto.response.ChapterResponse;
import edu.manipal.cse.questionbankservice.entities.Chapter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ChapterResponse> toListResponse(List<Chapter> chapters) {
        return chapters.stream().map(this::toResponse).collect(Collectors.toList());
    }
}