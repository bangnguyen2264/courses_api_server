package com.example.course.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class LessonFilter extends BaseFilter {
    @Schema(description = "Search by title or description")
    private String search;

    @Schema(description = "Filter by chapter id")
    private Long chapterId;

}
