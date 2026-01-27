package com.example.course.model.request;

import com.example.course.constant.DataType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LessonSectionRequest {
    private Long lessonId;
    private DataType dataType;
    private String title;
    private String description;
    private String content;
    private MultipartFile file;
    private Integer position;
}
