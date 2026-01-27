package com.example.course.model.entity;

import com.example.course.constant.ExamDuration;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamDuration duration;

    @ManyToOne
    private Subject subject;

    @ManyToMany
    private List<Quiz> quizzes;
}

