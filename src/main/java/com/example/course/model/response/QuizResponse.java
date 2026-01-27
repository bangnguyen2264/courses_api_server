package com.example.course.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizResponse {
    private Long id;
    private String question;
    private List<String> options;
    private boolean multipleChoice;
}

