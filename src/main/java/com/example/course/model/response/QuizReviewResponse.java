package com.example.course.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizReviewResponse {
    private Long id;
    private String question;
    private List<String> options;
    private String correctAnswers;
    private boolean multipleChoice;
}

