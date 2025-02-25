package com.pdc.questionbankservice.mappers;

import com.pdc.questionbankservice.dto.request.CreateQuestionRequest;
import com.pdc.questionbankservice.dto.request.UpdateQuestionRequest;
import com.pdc.questionbankservice.dto.response.QuestionListResponse;
import com.pdc.questionbankservice.dto.response.QuestionResponse;
import com.pdc.questionbankservice.entities.Question;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuestionMapper {
    private final ModelMapper modelMapper;

    public QuestionResponse toResponse(Question question) {
        return modelMapper.map(question, QuestionResponse.class);
    }

    public Question toEntity(CreateQuestionRequest request) {
        return modelMapper.map(request, Question.class);
    }

    public void updateEntity(UpdateQuestionRequest request, Question question) {
        modelMapper.map(request, question);
    }

    public List<QuestionResponse> toResponseList(List<Question> questions) {
        return questions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public QuestionListResponse toListResponse(List<Question> questions) {
        return QuestionListResponse.builder()
                .questions(toResponseList(questions))
                .totalQuestions(questions.size())
                .build();
    }
}

