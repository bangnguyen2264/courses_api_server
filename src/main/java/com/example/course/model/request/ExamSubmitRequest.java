package com.example.course.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExamSubmitRequest {

    // 1. Validate ID: Không được null và phải là số dương (>0)
    @NotNull(message = "Exam ID is required")
    @Positive(message = "Exam ID must be a positive number")
    private Long examId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    // 2. Validate Time: Không được null và phải >= 0
    @NotNull(message = "Time taken is required")
    @Min(value = 0, message = "Time taken cannot be negative")
    private Integer timeTaken;

    // 3. Validate Map answers:
    // - Bản thân cái Map không được null (dù nộp bài trắng thì phải gửi map rỗng {})
    @NotNull(message = "Answers data cannot be null")
    private Map<
            @Positive(message = "Quiz ID key must be positive") Long, // Key (QuizId) phải dương
            List<@Min(value = 0, message = "Answer index cannot be negative") Integer> // Index đáp án phải >= 0
            > answers;
}

//{
//        "examId": 1,
//        "userId": 2,
//        "timeTaken": 300,
//        "answers": {
//        "1": [1],
//        "2": [3],
//        "4": [2],
//        "5": [2],
//        "18": [0]
//        }
//        }
