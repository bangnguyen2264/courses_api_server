package com.example.course.service;

import com.example.course.filter.ChapterFilter;
import com.example.course.model.request.ChapterRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ChapterDetailResponse;
import com.example.course.model.response.ChapterResponse;

import java.util.List;

public interface ChapterService {

    ApiResponse<ChapterDetailResponse> getAll(ChapterFilter filter);
    ChapterResponse getById(Long id);
    ChapterResponse create(ChapterRequest chapterRequest);
    List<ChapterResponse> addAll(List<ChapterRequest> chapterRequests);
    ChapterResponse update(Long id, ChapterRequest chapterRequest);
    void delete(Long id);



}
