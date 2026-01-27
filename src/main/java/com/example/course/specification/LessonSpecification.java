package com.example.course.specification;

import com.example.course.filter.LessonFilter;
import com.example.course.model.entity.Lesson;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class LessonSpecification {

    public static Specification<Lesson> filter(LessonFilter filter) {
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

            // ===== FILTER BY CHAPTER =====
            if (filter.getChapterId() != null) {
                Join<Object, Object> chapterJoin = root.join("chapter", JoinType.INNER);

                predicate = cb.and(predicate,
                        cb.equal(chapterJoin.get("id"), filter.getChapterId()));
            }

            return predicate;
        };
    }
}
