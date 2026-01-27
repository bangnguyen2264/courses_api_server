package com.example.course.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data

public class QuizResultSubmission extends QuizReviewResponse {
    private String answer;
}
