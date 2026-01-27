package com.example.course.service;

import com.example.course.filter.ExamResultFilter;
import com.example.course.model.request.ExamSubmitRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ExamResultDetailResponse;
import com.example.course.model.response.ExamResultResponse;

public interface ExamResultService {
    ExamResultDetailResponse submit(ExamSubmitRequest request);
    ExamResultDetailResponse getById(Long id);
    ApiResponse<ExamResultResponse> getAll(ExamResultFilter examResultFilter);

    void delete(Long id);
}

