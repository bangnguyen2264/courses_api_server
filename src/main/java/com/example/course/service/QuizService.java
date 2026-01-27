package com.example.course.service;

import com.example.course.filter.QuizFilter;
import com.example.course.model.entity.Quiz;
import com.example.course.model.request.QuizRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.QuizResponse;
import com.example.course.model.response.QuizReviewResponse;

import java.util.List;

public interface QuizService {
    QuizResponse create(QuizRequest request);
    List<QuizResponse> addAll(List<QuizRequest> quizRequests);
    ApiResponse<QuizResponse> getAll(QuizFilter quizFilter);
    ApiResponse<QuizReviewResponse> getReview(QuizFilter quizFilter);
    QuizResponse getById(Long id);
    QuizResponse update(Long id, QuizRequest request);
    void delete(Long id);
}
