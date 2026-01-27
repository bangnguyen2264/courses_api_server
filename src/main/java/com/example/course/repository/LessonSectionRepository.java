package com.example.course.repository;

import com.example.course.model.entity.LessonSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LessonSectionRepository extends JpaRepository<LessonSection, Long>, JpaSpecificationExecutor<LessonSection> {
}
