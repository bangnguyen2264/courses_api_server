package com.example.course.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SubjectRequest {

    @NotBlank(message = "Subject name is required")
    private String name;

    private String description;

    private Integer position;

}
