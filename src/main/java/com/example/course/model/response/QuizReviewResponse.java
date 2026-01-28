package com.example.course.model.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizReviewResponse implements Serializable {
    private Long id;
    private String question;
    private List<String> options;
    private String correctAnswers;
    private boolean multipleChoice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

