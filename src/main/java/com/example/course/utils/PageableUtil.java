package com.example.course.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.course.filter.BaseFilter;
import org.springframework.data.domain.Sort;

public class PageableUtil {
    public static Pageable createPageable(BaseFilter filterRequest) {
        // Tạo sort
        Sort sort = Sort.by(
                filterRequest.getSort(),
                filterRequest.getField() != null ? filterRequest.getField() : "id"
        );

        // Tạo pageable
        return PageRequest.of(filterRequest.getPage(), filterRequest.getEntry(), sort);
    }
}
