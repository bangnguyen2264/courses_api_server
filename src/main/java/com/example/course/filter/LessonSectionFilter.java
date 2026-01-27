package com.example.course.filter;

import com.example.course.constant.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=true)
public class LessonSectionFilter extends  BaseFilter{

    @Schema(description = "Search by title or description")
    private String search;

    @Schema(description = "Filter by lesson id")
    private Long lessonId;

    @Schema(description = "Filter by data type (VIDEO, IMAGE, TEXT...)")
    private DataType dataType;
}
