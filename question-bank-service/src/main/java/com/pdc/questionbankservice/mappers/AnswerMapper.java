package com.pdc.questionbankservice.mappers;

import com.pdc.questionbankservice.dto.request.CreateAnswerRequest;
import com.pdc.questionbankservice.dto.response.AnswerResponse;
import com.pdc.questionbankservice.entities.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AnswerMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AnswerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Answer toEntity(CreateAnswerRequest request) {
        Answer answer = modelMapper.map(request, Answer.class);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setUpdatedAt(LocalDateTime.now());
        return answer;
    }

    public AnswerResponse toResponse(Answer answer) {
        return modelMapper.map(answer, AnswerResponse.class);
    }

    public List<AnswerResponse> toResponseList(List<Answer> answers) {
        return answers
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntity(CreateAnswerRequest request, Answer answer) {
        modelMapper.map(request, answer);
        answer.setUpdatedAt(LocalDateTime.now());
    }
}