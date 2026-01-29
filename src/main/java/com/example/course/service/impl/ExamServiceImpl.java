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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "exam")
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuizRepository quizRepository;
    private final SubjectRepository subjectRepository;
    private final ExamMapper examMapper;

    @Override
    @Transactional
    @CacheEvict(key = "'{exam-data}:list:'", allEntries = true)
    public ExamResponse create(ExamRequest request) {
        Exam exam = examMapper.toEntity(request);

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found with id: " + request.getSubjectId()));

        exam.setSubject(subject);

        List<Quiz> quizzes = new ArrayList<>();
        for (Long quizId : request.getQuizIds()) {
            quizzes.add(
                    quizRepository.findById(quizId)
                            .orElseThrow(() -> new NotFoundException("Quiz not found with id: " + quizId))
            );
        }

        exam.setQuizzes(quizzes);

        Exam saved = examRepository.save(exam);

        return examMapper.toResponse(saved);
    }

    @Override
    @Cacheable(key = "'{exam-data}:id:' + #id")
    public ExamResponse getById(Long id) {
        return examMapper.toResponse(findById(id));
    }

    @Override
    @Cacheable(key = "'{exam-data}:list:' + T(java.util.Objects).hash(#examFilter)")
    public ApiResponse<ExamResponse> getAll(ExamFilter examFilter) {
        Pageable pageable = PageableUtil.createPageable(examFilter);

        Page<Exam> exams =
                examRepository.findAll(ExamSpecification.filter(examFilter), pageable);

        return ApiResponse.fromPage(exams.map(examMapper::toResponse));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'{exam-data}:id:' + #id"),
            @CacheEvict(key = "'{exam-data}:list:'", allEntries = true)
    })
    public ExamResponse update(Long id, ExamRequest request) {
        Exam exam = findById(id);
        examMapper.updateEntityFromRequest(request, exam);
        Exam saved = examRepository.save(exam);
        return examMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'{exam-data}:id:' + #id"),
            @CacheEvict(key = "'{exam-data}:list:'", allEntries = true)
    })
    public void delete(Long id) {
        examRepository.delete(findById(id));
    }

    private Exam findById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exam not found with id: " + id));
    }
}
