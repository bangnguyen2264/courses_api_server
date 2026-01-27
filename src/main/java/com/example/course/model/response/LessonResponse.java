package com.example.course.model.response;

import com.example.course.model.entity.Lesson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {

    private Long id;

    private Long chapterId;

    private String title;

    private String description;

    private Integer position;

    public static LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .chapterId(lesson.getChapter().getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .position(lesson.getPosition())
                .build();
    }
}

