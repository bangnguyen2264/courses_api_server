package com.example.course.model.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ApiResponse<D> implements Serializable {
    private List<D> data;
    private int page;
    private long total;

    public static <D> ApiResponse<D> fromPage(Page<D> page) {
        return ApiResponse.<D>builder()
                .data(page.getContent())
                .page(page.getNumber())
                .total(page.getTotalElements())
                .build();
    }
}