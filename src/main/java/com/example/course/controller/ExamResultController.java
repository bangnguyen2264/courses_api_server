package com.example.course.controller;

import com.example.course.filter.ExamResultFilter;
import com.example.course.model.request.ExamSubmitRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ExamResultDetailResponse;
import com.example.course.model.response.ExamResultResponse;
import com.example.course.permission.ExamResultPermission;
import com.example.course.permission.UserPermission;
import com.example.course.security.author.RequirePermission;
import com.example.course.service.ExamResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam-result")
@RequiredArgsConstructor
public class ExamResultController {
    private final ExamResultService examResultService;

    @PostMapping("/submit")
    @RequirePermission(resource = UserPermission.class, idParam = "#request.userId")
    public ResponseEntity<ExamResultDetailResponse> submit(@Valid @RequestBody ExamSubmitRequest request) {
        return new ResponseEntity<>(examResultService.submit(request), HttpStatus.CREATED);
    }

    @GetMapping
    @RequirePermission(resource = UserPermission.class, idParam = "#examResultFilter.userId" )
    public ResponseEntity<ApiResponse<ExamResultResponse>> getAll(@Valid @ParameterObject ExamResultFilter examResultFilter){
        return new ResponseEntity<>(examResultService.getAll(examResultFilter), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @RequirePermission(resource = ExamResultPermission.class, idParam = "#id")
    public ResponseEntity<ExamResultDetailResponse> getById(@PathVariable Long id){
        return new ResponseEntity<>(examResultService.getById(id), HttpStatus.OK);
    }



}
