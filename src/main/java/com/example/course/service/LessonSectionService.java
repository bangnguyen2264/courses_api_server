package com.example.course.service;

import com.example.course.filter.LessonSectionFilter;
import com.example.course.model.response.LessonSectionResponse;
import com.example.course.model.request.LessonSectionRequest;
import com.example.course.model.response.ApiResponse;

import java.util.List;

public interface LessonSectionService {
    ApiResponse<LessonSectionResponse> getAll(LessonSectionFilter lessonSectionFilter);
    LessonSectionResponse getById(Long id);
    LessonSectionResponse create(LessonSectionRequest lessonSectionRequest);
    List<LessonSectionResponse> addAll(List<LessonSectionRequest> sectionRequests);
    LessonSectionResponse update(Long id,LessonSectionRequest lessonSectionRequest);
    void delete(Long id);
}
