package com.example.course.controller;

import com.example.course.filter.QuizFilter;
import com.example.course.model.entity.Quiz;
import com.example.course.model.request.QuizRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.QuizResponse;
import com.example.course.model.response.QuizReviewResponse;
import com.example.course.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz(@Valid @ParameterObject QuizFilter quizFilter) {
        return ResponseEntity.ok(quizService.getAll(quizFilter));
    }
    @GetMapping("/review")
    public ResponseEntity<ApiResponse<QuizReviewResponse>> getReview(@Valid @ParameterObject QuizFilter quizFilter) {
        return ResponseEntity.ok(quizService.getReview(quizFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getById(id));
    }

    @PostMapping
    public ResponseEntity<QuizResponse> create(@Valid @RequestBody QuizRequest quizRequest) {
        return new ResponseEntity<>(quizService.create(quizRequest), HttpStatus.CREATED);
    }
    @PostMapping("/batch")
    public ResponseEntity<List<QuizResponse>> create(@Valid @RequestBody List<QuizRequest> quizRequests) {
        return new ResponseEntity<>(quizService.addAll(quizRequests), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuizResponse> update(@PathVariable Long id, @Valid @RequestBody QuizRequest quizRequest) {
        return ResponseEntity.ok(quizService.update(id, quizRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
