package com.example.course.service;

import com.example.course.filter.ExamFilter;
import com.example.course.model.request.ExamRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ExamResponse;

public interface ExamService {
    ExamResponse create(ExamRequest request);
    ExamResponse getById(Long id);
    ApiResponse<ExamResponse> getAll(ExamFilter examFilter);
    ExamResponse update(Long id, ExamRequest request);
    void delete(Long id);
}
