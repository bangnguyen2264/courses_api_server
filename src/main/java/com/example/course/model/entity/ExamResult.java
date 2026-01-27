package com.example.course.model.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private Double score; // Nên dùng Double (ví dụ: 8.5 điểm)

    private int correct;  // Số câu đúng

    private int incorrect; // Số câu sai

    @Column(columnDefinition = "TEXT") // Dùng TEXT để lưu được chuỗi JSON dài
    private String submissionHistory;

    private Integer timeTaken; // Thời gian làm bài (giây)
}