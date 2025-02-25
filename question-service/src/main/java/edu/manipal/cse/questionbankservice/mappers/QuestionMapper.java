package edu.manipal.cse.questionbankservice.mappers;

import edu.manipal.cse.questionbankservice.dto.request.CreateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.response.QuestionListResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionResponse;
import edu.manipal.cse.questionbankservice.entities.Question;
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

