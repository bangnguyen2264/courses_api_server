package com.example.course.service;

import com.example.course.filter.SubjectFilter;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.SubjectDetailResponse;
import com.example.course.model.response.SubjectResponse;

import java.util.List;

public interface SubjectService {
    ApiResponse<SubjectResponse> getAll(SubjectFilter filter);
    SubjectDetailResponse getById(Long id);
    SubjectResponse create(SubjectRequest subjectRequest);
    List<SubjectResponse> addAll(List<SubjectRequest> subjectRequests);
    SubjectResponse update(Long id, SubjectRequest subjectRequest);
    void delete(Long id);

}
