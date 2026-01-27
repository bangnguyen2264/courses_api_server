package com.example.course.controller;

import com.example.course.filter.LessonFilter;
import com.example.course.model.request.LessonRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.LessonResponse;
import com.example.course.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<ApiResponse<LessonResponse>> getAll(@Valid @ParameterObject LessonFilter lessonFilter) {
        return ResponseEntity.ok(lessonService.getAll(lessonFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getById(id));
    }

    @PostMapping
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest lessonRequest) {
        return new ResponseEntity<>(lessonService.create(lessonRequest), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<LessonResponse>> addAll(
            @RequestBody List<@Valid LessonRequest> lessonRequests) {
        List<LessonResponse> responses = lessonService.addAll(lessonRequests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LessonResponse> update(@PathVariable Long id, @Valid @RequestBody LessonRequest lessonRequest) {
        return ResponseEntity.ok(lessonService.update(id, lessonRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
