package com.example.course.model.response;

import com.example.course.model.entity.Lesson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse implements Serializable {

    private Long id;

    private Long chapterId;

    private String title;

    private String description;

    private Integer position;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    public static LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .chapterId(lesson.getChapter().getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .position(lesson.getPosition())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .createdBy(lesson.getCreatedBy())
                .updatedBy(lesson.getUpdatedBy())
                .build();
    }
}

