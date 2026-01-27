package com.example.course.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springdoc.core.annotations.ParameterObject;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChapterFilter extends BaseFilter {
    @Schema(description = "Search by title or description")
    private String search;

    @Schema(description = "Filter by subject id")
    private Long subjectId;

}
