package com.example.course.filter;

import com.example.course.constant.ExamDuration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExamFilter extends BaseFilter {
    // tìm theo title
    private String search;

    // lọc theo môn học
    private Long subjectId;

    // lọc theo mốc thời gian
    private ExamDuration duration;
}
