package com.example.course.model.response;

import com.example.course.model.entity.Chapter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@SuperBuilder

public class ChapterDetailResponse extends ChapterResponse implements Serializable {

    private List<LessonResponse> lessons;


    public static ChapterDetailResponse toResponse(Chapter chapter) {
        return ChapterDetailResponse.builder()
                .id(chapter.getId())
                .subjectId(chapter.getSubject().getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .position(chapter.getPosition())
                .lessons(chapter.getLessons().stream().map(LessonResponse::toResponse).collect(Collectors.toList()))
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .createdBy(chapter.getCreatedBy())
                .updatedBy(chapter.getUpdatedBy())
                .build();
    }
}
