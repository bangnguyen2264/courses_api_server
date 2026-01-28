package com.example.course.model.response;

import com.example.course.model.entity.Chapter;
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
public class ChapterResponse implements Serializable {

    private Long id;

    private Long subjectId;

    private String title;

    private String description;

    private Integer position;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    public static ChapterResponse toResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .position(chapter.getPosition())
                .subjectId(chapter.getSubject().getId())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .createdBy(chapter.getCreatedBy())
                .updatedBy(chapter.getUpdatedBy())
                .build();
    }

}
