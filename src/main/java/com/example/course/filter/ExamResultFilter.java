package com.example.course.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class ExamResultFilter extends BaseFilter {

    // lọc theo user
    private Long userId;

    // lọc theo exam
    private Long examId;

    // lọc theo điểm
    private Integer scoreFrom;
    private Integer scoreTo;

    // lọc theo thời gian làm bài
    private LocalDateTime submittedFrom;
    private LocalDateTime submittedTo;
}
