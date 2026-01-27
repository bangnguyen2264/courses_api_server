package com.example.course.specification;

import com.example.course.filter.ExamFilter;
import com.example.course.filter.UserFilter;
import com.example.course.model.entity.Exam;
import com.example.course.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class ExamSpecification {

    public static Specification<Exam> filter(ExamFilter filter) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // search theo title
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("title")),
                                "%" + filter.getSearch().toLowerCase() + "%"));
            }

            // filter theo subject
            if (filter.getSubjectId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("subject").get("id"), filter.getSubjectId()));
            }

            // filter theo duration (enum)
            if (filter.getDuration() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("duration"), filter.getDuration()));
            }


            return predicate;
        };
    }
}
