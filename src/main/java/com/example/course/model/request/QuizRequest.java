package com.example.course.model.request;

import com.example.course.model.entity.Quiz;
import com.example.course.utils.JsonUtils;
import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private String question;
    private List<String> options;
    private List<Integer> correctAnswers;
    private boolean multipleChoice;
    private Long subjectId;

    public static Quiz toEntity(QuizRequest quizRequest){
        return Quiz.builder()
                .question(quizRequest.getQuestion())
                .options(JsonUtils.toJson(quizRequest.getOptions()))
                .correctAnswers(JsonUtils.toJson(quizRequest.getCorrectAnswers()))
                .multipleChoice(quizRequest.isMultipleChoice())
                .build();
    }
}

