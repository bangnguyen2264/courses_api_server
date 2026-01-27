package com.example.course.model.request;

import com.example.course.constant.ExamDuration;
import lombok.Data;

import java.util.List;

@Data
public class ExamRequest {
    private String title;
    private ExamDuration duration;
    private Long subjectId;
    private List<Long> quizIds;
}
