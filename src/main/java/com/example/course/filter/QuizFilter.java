package com.example.course.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=true)
public class QuizFilter extends BaseFilter {

    @Schema(description = "Search by question")
    private String search;

    @Schema(description = "Filter by subject id")
    private Long subjectId;

    @Schema(description = "Filter by multiple choice type")
    private Boolean multipleChoice;

    // === THÊM MỚI ===
    @Schema(description = "Filter by exam id")
    private Long examId;
}