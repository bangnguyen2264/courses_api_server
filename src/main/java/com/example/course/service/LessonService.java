package com.example.course.service;

import com.example.course.filter.LessonFilter;
import com.example.course.model.request.LessonRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.LessonResponse;

import java.util.List;

public interface LessonService {
    ApiResponse<LessonResponse> getAll(LessonFilter lessonFilter);
    LessonResponse getById(Long id);
    LessonResponse create(LessonRequest lessonRequest);
    List<LessonResponse> addAll(List<LessonRequest> lessonRequests);
    LessonResponse update(Long id,LessonRequest lessonRequest);
    void delete(Long id);
}
