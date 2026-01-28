package com.example.course.model.response;

import com.example.course.model.entity.ExamResult;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExamResultResponse implements Serializable {
    private Long id;
    private Long examId;
    private String examTitle;
    private Double score;
    private int correct;
    private int incorrect;
    private Integer timeTaken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static ExamResultResponse toResponse(ExamResult entity) {
        return ExamResultResponse.builder()
                .id(entity.getId())
                .examId(entity.getExam().getId())
                .examTitle(entity.getExam() != null ? entity.getExam().getTitle() : null)
                .score(entity.getScore())
                .correct(entity.getCorrect())
                .incorrect(entity.getIncorrect())
                .timeTaken(entity.getTimeTaken())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}