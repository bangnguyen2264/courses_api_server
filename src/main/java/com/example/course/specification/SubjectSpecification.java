package com.example.course.specification;

import com.example.course.filter.SubjectFilter;
import com.example.course.model.entity.Subject;
import org.springframework.data.jpa.domain.Specification;

public class SubjectSpecification {

    public static Specification<Subject> filter(SubjectFilter filter) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // ===== SEARCH BY NAME =====
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"
                        )
                );
            }

            // ===== FILTER BY STATUS =====
            if (filter.getStatus() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("status"), filter.getStatus()));
            }

            return predicate;
        };
    }
}
