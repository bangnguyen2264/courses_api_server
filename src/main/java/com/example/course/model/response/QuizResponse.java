package com.example.course.model.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuizResponse implements Serializable {
    private Long id;
    private String question;
    private List<String> options;
    private boolean multipleChoice;
}

