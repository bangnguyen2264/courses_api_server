package com.example.course.specification;

import com.example.course.filter.QuizFilter;
import com.example.course.model.entity.Exam; // Import Exam
import com.example.course.model.entity.Quiz;
import com.example.course.model.entity.Subject;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List; // Import List

public class QuizSpecification {

    public static Specification<Quiz> filter(QuizFilter filter) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // 1. SEARCH QUESTION
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String keyword = "%" + filter.getSearch().toLowerCase() + "%";
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("question")), keyword)
                );
            }

            // 2. FILTER BY SUBJECT
            if (filter.getSubjectId() != null) {
                Join<Quiz, Subject> subjectJoin = root.join("subject", JoinType.INNER);
                predicate = cb.and(predicate,
                        cb.equal(subjectJoin.get("id"), filter.getSubjectId()));
            }

            // 3. FILTER BY MULTIPLE CHOICE
            if (filter.getMultipleChoice() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("multipleChoice"), filter.getMultipleChoice()));
            }

            // === 4. FILTER BY EXAM ID (MỚI) ===
            if (filter.getExamId() != null) {
                // Join bảng quizzes -> exams
                // Lưu ý: Vì quan hệ là List<Exam> exams, nên Join<Quiz, Exam>
                Join<Quiz, Exam> examJoin = root.join("exams", JoinType.INNER);

                predicate = cb.and(predicate,
                        cb.equal(examJoin.get("id"), filter.getExamId()));

                // (Tùy chọn) Để tránh trùng lặp dữ liệu khi join n-n,
                // bạn có thể cần set distinct cho query
                query.distinct(true);
            }

            return predicate;
        };
    }
}