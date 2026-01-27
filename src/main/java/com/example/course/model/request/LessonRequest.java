package com.example.course.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LessonRequest {

    @NotNull
    private Long chapterId;

    @NotBlank
    private String title;

    private String description;

    private Integer position;

}

