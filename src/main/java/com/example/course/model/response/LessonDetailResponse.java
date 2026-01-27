package com.example.course.model.response;

import com.example.course.model.entity.Lesson;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@SuperBuilder
public class LessonDetailResponse extends LessonResponse implements Serializable {

    private List<LessonSectionResponse> sections;

    public static LessonDetailResponse toResponse(Lesson lesson) {
        return LessonDetailResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .chapterId(lesson.getChapter().getId())
                .sections(lesson.getSections().stream().map(LessonSectionResponse::toResponse).collect(Collectors.toList()))
                .position(lesson.getPosition())
                .build();
    }
}

