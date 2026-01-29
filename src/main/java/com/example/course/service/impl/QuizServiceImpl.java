package com.example.course.service.impl;

import com.example.course.exception.BadRequestException;
import com.example.course.exception.NotFoundException;
import com.example.course.filter.QuizFilter;
import com.example.course.mapper.QuizMapper;
import com.example.course.model.entity.Quiz;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.QuizRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.QuizResponse;
import com.example.course.model.response.QuizReviewResponse;
import com.example.course.repository.QuizRepository;
import com.example.course.repository.SubjectRepository;
import com.example.course.service.QuizService;
import com.example.course.specification.QuizSpecification;
import com.example.course.utils.PageableUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final SubjectRepository subjectRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional
    @CacheEvict(value = "quiz-list", allEntries = true)
    public QuizResponse create(QuizRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        Quiz quiz = quizMapper.toEntity(request, subject);
        Quiz saved = quizRepository.save(quiz);

        return quizMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "quiz-list", allEntries = true)
    public List<QuizResponse> addAll(List<QuizRequest> quizRequests) {
        if (quizRequests == null || quizRequests.isEmpty()) {
            throw new BadRequestException("No quiz request found");
        }

        List<Quiz> quizList = new ArrayList<>();

        for (QuizRequest quizRequest : quizRequests) {
            Subject subject = subjectRepository.findById(quizRequest.getSubjectId())
                    .orElseThrow(() ->
                            new NotFoundException("Subject not found with id: " + quizRequest.getSubjectId())
                    );

            Quiz quiz = quizMapper.toEntity(quizRequest, subject);
            quizRepository.save(quiz);
            quizList.add(quiz);
        }

        return quizList.stream()
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(
            value = "quiz-list",
            key = "'{quiz-data}:list:' + #quizFilter.toString()",
            unless = "#result == null"
    )
    public ApiResponse<QuizResponse> getAll(QuizFilter quizFilter) {
        Pageable pageable = PageableUtil.createPageable(quizFilter);
        Page<Quiz> quizPage = quizRepository.findAll(QuizSpecification.filter(quizFilter), pageable);
        Page<QuizResponse> quizResponsePage = quizPage.map(quizMapper::toResponse);
        return ApiResponse.fromPage(quizResponsePage);
    }

    @Override
    @Cacheable(
            value = "quiz-list",
            key = "'{quiz-data}:review-list:' + #quizFilter.toString()",
            unless = "#result == null"
    )
    public ApiResponse<QuizReviewResponse> getReview(QuizFilter quizFilter) {
        Pageable pageable = PageableUtil.createPageable(quizFilter);
        Page<Quiz> quizPage = quizRepository.findAll(QuizSpecification.filter(quizFilter), pageable);
        Page<QuizReviewResponse> quizResponsePage = quizPage.map(quizMapper::toReviewResponse);
        return ApiResponse.fromPage(quizResponsePage);
    }

    @Override
    @Cacheable(
            value = "quiz",
            key = "'{quiz-data}:id:' + #id"
    )
    public QuizResponse getById(Long id) {
        return quizMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "quiz", key = "'{quiz-data}:id:' + #id"),
                    @CacheEvict(value = "quiz-list", allEntries = true)
            }
    )
    public QuizResponse update(Long id, QuizRequest request) {
        Quiz quiz = findById(id);
        quizMapper.updateEntityFromRequest(request, quiz);
        Quiz saved = quizRepository.save(quiz);
        return quizMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "quiz", key = "'{quiz-data}:id:' + #id"),
                    @CacheEvict(value = "quiz-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        Quiz quiz = findById(id);
        quizRepository.delete(quiz);
    }

    private Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found by id: " + id));
    }
}