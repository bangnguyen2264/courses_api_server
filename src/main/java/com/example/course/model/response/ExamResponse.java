package com.example.course.model.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExamResponse implements Serializable {
    private Long id;
    private String title;
    private int duration;
}
