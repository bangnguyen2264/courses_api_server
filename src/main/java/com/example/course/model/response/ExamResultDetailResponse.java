package com.example.course.model.response;

import com.example.course.model.entity.ExamResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ExamResultDetailResponse extends ExamResultResponse {

    private List<QuizResultSubmission> quizResultSubmissionList;


    public static ExamResultDetailResponse toResponse(ExamResult entity, List<QuizResultSubmission> quizResultSubmissionList) {
        return ExamResultDetailResponse.builder()
                .id(entity.getId())
                .examId(entity.getExam().getId())
                .examTitle(entity.getExam() != null ? entity.getExam().getTitle() : null)
                .score(entity.getScore())
                .correct(entity.getCorrect())
                .quizResultSubmissionList(quizResultSubmissionList)
                .incorrect(entity.getIncorrect())
                .timeTaken(entity.getTimeTaken())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}