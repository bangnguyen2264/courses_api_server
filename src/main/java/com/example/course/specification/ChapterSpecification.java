package com.example.course.specification;

import com.example.course.filter.ChapterFilter;
import com.example.course.model.entity.Chapter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ChapterSpecification {

    public static Specification<Chapter> filter(ChapterFilter filter) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // ===== SEARCH title + description =====
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String keyword = "%" + filter.getSearch().toLowerCase() + "%";

                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("title")), keyword),
                                cb.like(cb.lower(root.get("description")), keyword)
                        )
                );
            }

            // ===== FILTER BY SUBJECT =====
            if (filter.getSubjectId() != null) {
                Join<Object, Object> subjectJoin = root.join("subject", JoinType.INNER);

                predicate = cb.and(predicate,
                        cb.equal(subjectJoin.get("id"), filter.getSubjectId()));
            }

            return predicate;
        };
    }
}
