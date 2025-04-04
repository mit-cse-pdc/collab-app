package edu.manipal.cse.questionbankservice.mappers;

import edu.manipal.cse.questionbankservice.dto.request.CreateAnswerRequest;
import edu.manipal.cse.questionbankservice.dto.request.CreateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.request.UpdateQuestionRequest;
import edu.manipal.cse.questionbankservice.dto.response.QuestionListResponse;
import edu.manipal.cse.questionbankservice.dto.response.QuestionResponse;
import edu.manipal.cse.questionbankservice.entities.Answer;
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
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setText(request.getText());
        question.setQuestionType(request.getQuestionType());
        question.setFacultyId(request.getFacultyId());

        if (request.getAnswers() != null) {
            for (CreateAnswerRequest answerRequest : request.getAnswers()) {
                Answer answer = new Answer();
                answer.setText(answerRequest.getText());
                answer.setIsCorrect(answerRequest.getIsCorrect());
                answer.setExplanation(answerRequest.getExplanation());
                answer.setQuestion(question); // Set the question field
                question.getAnswers().add(answer);
            }
        }

        return question;
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

