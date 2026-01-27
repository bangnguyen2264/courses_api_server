package com.example.course.controller;

import com.example.course.filter.LessonSectionFilter;
import com.example.course.model.entity.LessonSection;
import com.example.course.model.request.LessonSectionRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.LessonSectionResponse;
import com.example.course.service.LessonSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-section")
@RequiredArgsConstructor
public class LessonSectionController {
    private final LessonSectionService lessonSectionService;

    @GetMapping
    public ResponseEntity<ApiResponse<LessonSectionResponse>> getAll(@Valid @ParameterObject LessonSectionFilter lessonSectionFilter) {
        return ResponseEntity.ok(lessonSectionService.getAll(lessonSectionFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonSectionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonSectionService.getById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LessonSectionResponse> create(
            @Valid @ModelAttribute LessonSectionRequest lessonSectionRequest) {
        LessonSectionResponse response = lessonSectionService.create(lessonSectionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<LessonSectionResponse>> addAll(
            @RequestBody List<@Valid LessonSectionRequest> sectionRequests) {
        List<LessonSectionResponse> responses = lessonSectionService.addAll(sectionRequests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LessonSectionResponse> update(@PathVariable Long id, @Valid @RequestBody LessonSectionRequest lessonSectionRequest) {
        return ResponseEntity.ok(lessonSectionService.update(id, lessonSectionRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonSectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
