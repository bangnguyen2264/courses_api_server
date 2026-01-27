package com.example.course.service.impl;

import com.example.course.exception.NotFoundException;
import com.example.course.filter.ExamFilter;
import com.example.course.mapper.ExamMapper;
import com.example.course.model.entity.Exam;
import com.example.course.model.entity.Quiz;
import com.example.course.model.entity.Subject;
import com.example.course.model.request.ExamRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ExamResponse;
import com.example.course.repository.ExamRepository;
import com.example.course.repository.QuizRepository;
import com.example.course.repository.SubjectRepository;
import com.example.course.service.ExamService;
import com.example.course.specification.ExamSpecification;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuizRepository quizRepository;
    private final SubjectRepository subjectRepository;
    private final ExamMapper examMapper;

    @Override
    @Transactional
    @CacheEvict(value = "exam-list", allEntries = true)
    public ExamResponse create(ExamRequest request) {
        Exam exam = examMapper.toEntity(request);

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() ->
                        new NotFoundException("Subject not found with id: " + request.getSubjectId())
                );

        exam.setSubject(subject);

        List<Quiz> quizList = new ArrayList<>();

        for (Long quizId : request.getQuizIds()) {
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() ->
                            new NotFoundException("Quiz not found with id: " + quizId)
                    );
            quizList.add(quiz);
        }

        exam.setQuizzes(quizList);

        Exam saved = examRepository.save(exam);

        return examMapper.toResponse(saved);
    }

    @Override
    @Cacheable(value = "exam", key = "#id")
    public ExamResponse getById(Long id) {
        return examMapper.toResponse(findById(id));
    }

    @Override
    @Cacheable(
            value = "exam-list",
            key = "T(java.util.Objects).hash(#examFilter)"
    )
    public ApiResponse<ExamResponse> getAll(ExamFilter examFilter) {
        Pageable pageable = PageableUtil.createPageable(examFilter);

        Page<Exam> exams =
                examRepository.findAll(ExamSpecification.filter(examFilter), pageable);

        Page<ExamResponse> results =
                exams.map(examMapper::toResponse);

        return ApiResponse.fromPage(results);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "exam", key = "#id"),
                    @CacheEvict(value = "exam-list", allEntries = true)
            }
    )
    public ExamResponse update(Long id, ExamRequest request) {
        Exam exam = findById(id);
        examMapper.updateEntityFromRequest(request, exam);
        Exam saved = examRepository.save(exam);
        return examMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "exam", key = "#id"),
                    @CacheEvict(value = "exam-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        Exam exam = findById(id);
        examRepository.delete(exam);
    }

    private Exam findById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Exam not found with id: " + id)
                );
    }
}
