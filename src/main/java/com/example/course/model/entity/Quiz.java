package com.example.course.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String question;

    // JSON: ["A", "B", "C", "D"]
    @Column(columnDefinition = "TEXT")
    private String options;

    // JSON: [0,2] (multiple choice)
    @Column(columnDefinition = "TEXT")
    private String correctAnswers;

    private boolean multipleChoice;

    @ManyToOne
    private Subject subject;

    @ManyToMany(mappedBy = "quizzes")
    @ToString.Exclude // Bắt buộc để tránh lỗi StackOverflow
    @JsonIgnore       // Tránh lỗi khi serialize JSON (nếu return entity trực tiếp)
    private List<Exam> exams;
}

