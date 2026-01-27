package com.example.course.controller;

import com.example.course.filter.ChapterFilter;
import com.example.course.model.entity.Chapter;
import com.example.course.model.request.ChapterRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ChapterDetailResponse;
import com.example.course.model.response.ChapterResponse;
import com.example.course.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chapter")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    public ResponseEntity<ChapterResponse> create(@RequestBody ChapterRequest chapterRequest) {
        return new ResponseEntity<>(chapterService.create(chapterRequest), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ChapterResponse>> addAll(
            @RequestBody List<@Valid ChapterRequest> chapterRequests) {
        List<ChapterResponse> responses = chapterService.addAll(chapterRequests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponse> getById(@PathVariable Long id) {
        return new ResponseEntity<>(chapterService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ChapterDetailResponse>> getAll(@Valid @ParameterObject ChapterFilter chapterFilter) {
        return new ResponseEntity<>(chapterService.getAll(chapterFilter), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChapterResponse> update(@PathVariable Long id, @RequestBody ChapterRequest chapterRequest) {
        return new ResponseEntity<>(chapterService.update(id, chapterRequest), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chapterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
