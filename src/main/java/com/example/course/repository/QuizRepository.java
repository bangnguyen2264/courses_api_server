package com.example.course.repository;

import com.example.course.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> , JpaSpecificationExecutor<Quiz> {
    List<Quiz> findBySubjectId(Long subjectId);
    List<Quiz> findByExams_Id(Long examId);
}
