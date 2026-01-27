package com.example.course.specification;

import com.example.course.exception.BadRequestException;
import com.example.course.filter.UserFilter;
import com.example.course.model.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

    public static Specification<User> filter(UserFilter filter) {
        if( filter.getDobFrom().isAfter(filter.getDobTo()) ) {
            throw new BadRequestException("Dobfrom should be after DobTo");
        }
        return (root, query, cb) -> {

            var predicate = cb.conjunction();
            LocalDate today = LocalDate.now();

            // ===== SEARCH =====
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String keyword = "%" + filter.getSearch().toLowerCase() + "%";

                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("fullName")), keyword),
                                cb.like(cb.lower(root.get("email")), keyword),
                                cb.like(cb.lower(root.get("phoneNumber")), keyword)
                        )
                );
            }

            // ===== GENDER =====
            if (filter.getGender() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("gender"), filter.getGender()));
            }

            // ===== ROLE =====
            if (filter.getRole() != null && !filter.getRole().isBlank()) {
                Join<Object, Object> roleJoin = root.join("role", JoinType.INNER);
                predicate = cb.and(predicate,
                        cb.equal(roleJoin.get("name"), filter.getRole()));
            }

            // ===== DOB RANGE (with max = today) =====
            LocalDate dobFrom = filter.getDobFrom();
            LocalDate dobTo = filter.getDobTo();

            // clamp dobTo to today
            if (dobTo != null && dobTo.isAfter(today)) {
                dobTo = today;
            }

            if (dobFrom != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("dob"), dobFrom));
            }

            if (dobTo != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("dob"), dobTo));
            }

            return predicate;
        };
    }
}
