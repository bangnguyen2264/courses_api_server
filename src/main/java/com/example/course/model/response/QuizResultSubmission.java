package com.example.course.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data

public class QuizResultSubmission extends QuizReviewResponse implements Serializable {
    private String answer;
}
