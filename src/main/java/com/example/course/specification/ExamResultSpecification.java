package com.example.course.specification;

import com.example.course.filter.ExamResultFilter;
import com.example.course.model.entity.ExamResult;
import org.springframework.data.jpa.domain.Specification;

public class ExamResultSpecification {

    public static Specification<ExamResult> filter(ExamResultFilter filter) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // filter theo user
            if (filter.getUserId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("user").get("id"), filter.getUserId()));
            }

            // filter theo exam
            if (filter.getExamId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("exam").get("id"), filter.getExamId()));
            }

            // filter theo score from
            if (filter.getScoreFrom() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("score"), filter.getScoreFrom()));
            }

            // filter theo score to
            if (filter.getScoreTo() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("score"), filter.getScoreTo()));
            }

            // filter theo thời gian nộp bài
            if (filter.getSubmittedFrom() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"), filter.getSubmittedFrom()));
            }

            if (filter.getSubmittedTo() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"), filter.getSubmittedTo()));
            }

            return predicate;
        };
    }
}
