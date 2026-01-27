package com.example.course.controller;

import com.example.course.filter.SubjectFilter;
import com.example.course.model.request.SubjectRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.SubjectResponse;
import com.example.course.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> getAll(@Valid @ParameterObject SubjectFilter filter) {
        return new ResponseEntity<>(subjectService.getAll(filter), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getById(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(subjectService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        return new ResponseEntity<>(subjectService.create(request), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SubjectResponse>> addAll(
            @RequestBody List<@Valid SubjectRequest> subjectRequests) {
        List<SubjectResponse> responses = subjectService.addAll(subjectRequests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubjectResponse> update(@PathVariable Long id, @RequestBody SubjectRequest request) {
        return new ResponseEntity<>(subjectService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        subjectService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
