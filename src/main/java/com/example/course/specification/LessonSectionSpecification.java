package com.example.course.specification;

import com.example.course.filter.LessonSectionFilter;
import com.example.course.model.entity.LessonSection;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class LessonSectionSpecification {

    public static Specification<LessonSection> filter(LessonSectionFilter filter) {
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

            // ===== FILTER BY LESSON =====
            if (filter.getLessonId() != null) {
                Join<Object, Object> lessonJoin = root.join("lesson", JoinType.INNER);

                predicate = cb.and(predicate,
                        cb.equal(lessonJoin.get("id"), filter.getLessonId()));
            }

            // ===== FILTER BY DATA TYPE =====
            if (filter.getDataType() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("dataType"), filter.getDataType()));
            }

            return predicate;
        };
    }
}
