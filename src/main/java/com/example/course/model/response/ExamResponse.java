package com.example.course.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamResponse {
    private Long id;
    private String title;
    private int duration;
}
