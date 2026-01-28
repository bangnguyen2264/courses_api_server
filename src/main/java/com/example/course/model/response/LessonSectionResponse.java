package com.example.course.model.response;

import com.example.course.constant.DataType;
import com.example.course.model.entity.LessonSection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonSectionResponse implements Serializable {

    private Long id;

    private Long lessonId;

    private DataType dataType;

    private String title;

    private String description;

    private String content;

    private String dataPath;

    private Integer position;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    public static LessonSectionResponse toResponse(LessonSection lessonSection) {
        return LessonSectionResponse.builder()
                .id(lessonSection.getId())
                .lessonId(lessonSection.getLesson().getId())
                .dataType(lessonSection.getDataType())
                .title(lessonSection.getTitle())
                .description(lessonSection.getDescription())
                .content(lessonSection.getContent())
                .dataPath(lessonSection.getDataPath())
                .position(lessonSection.getPosition())
                .createdAt(lessonSection.getCreatedAt())
                .updatedAt(lessonSection.getUpdatedAt())
                .createdBy(lessonSection.getCreatedBy())
                .updatedBy(lessonSection.getUpdatedBy())
                .build();
    }
}
