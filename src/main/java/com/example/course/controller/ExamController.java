package com.example.course.controller;

import com.example.course.filter.ExamFilter;
import com.example.course.model.entity.Exam;
import com.example.course.model.request.ExamRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ExamResponse;
import com.example.course.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public ResponseEntity<ApiResponse<ExamResponse>> getAll(@Valid @ParameterObject ExamFilter examFilter) {
        return ResponseEntity.ok(examService.getAll(examFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ExamResponse> create(@RequestBody @Valid ExamRequest examRequest) {
        return new  ResponseEntity<>(examService.create(examRequest), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExamResponse> update(@PathVariable Long id, @RequestBody @Valid ExamRequest examRequest) {
        return ResponseEntity.ok(examService.update(id, examRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
