package com.example.course.service.impl;

import com.example.course.exception.NotFoundException;
import com.example.course.filter.ExamResultFilter;
import com.example.course.model.entity.*;
import com.example.course.model.request.ExamSubmitRequest;
import com.example.course.model.response.*;
import com.example.course.repository.*;
import com.example.course.service.ExamResultService;
import com.example.course.specification.ExamResultSpecification;
import com.example.course.utils.PageableUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "exam-result")
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @CacheEvict(key = "'{exam-result-data}:list:'", allEntries = true)
    public ExamResultDetailResponse submit(ExamSubmitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found id: " + request.getUserId()));

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new NotFoundException("Exam not found id: " + request.getExamId()));

        List<Quiz> quizzes = quizRepository.findByExams_Id(request.getExamId());

        Map<Long, List<Integer>> answers =
                request.getAnswers() != null ? request.getAnswers() : new HashMap<>();

        GradingResult grading = calculateGrading(quizzes, answers);

        ExamResult result = ExamResult.builder()
                .user(user)
                .exam(exam)
                .score(grading.totalScore)
                .correct(grading.correctCount)
                .incorrect(grading.incorrectCount)
                .timeTaken(request.getTimeTaken())
                .submissionHistory(toJson(answers))
                .build();

        examResultRepository.save(result);

        List<QuizResultSubmission> details = buildSubmissionDetails(quizzes, answers);

        return ExamResultDetailResponse.toResponse(result, details);
    }

    @Override
    @Cacheable(key = "'{exam-result-data}:id:' + #id")
    public ExamResultDetailResponse getById(Long id) {
        ExamResult result = examResultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Result not found"));

        Map<Long, List<Integer>> userAnswers = parseHistoryJson(result.getSubmissionHistory());
        List<Quiz> quizzes = quizRepository.findByExams_Id(result.getExam().getId());

        return ExamResultDetailResponse.toResponse(
                result,
                buildSubmissionDetails(quizzes, userAnswers)
        );
    }

    @Override
    @Cacheable(key = "'{exam-result-data}:list:' + T(java.util.Objects).hash(#filter)")
    public ApiResponse<ExamResultResponse> getAll(ExamResultFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);

        Page<ExamResult> page =
                examResultRepository.findAll(ExamResultSpecification.filter(filter), pageable);

        return ApiResponse.fromPage(page.map(ExamResultResponse::toResponse));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "'{exam-result-data}:id:' + #id"),
            @CacheEvict(key = "'{exam-result-data}:list:'", allEntries = true)
    })
    public void delete(Long id) {
        if (!examResultRepository.existsById(id)) {
            throw new NotFoundException("Result not found id: " + id);
        }
        examResultRepository.deleteById(id);
    }

    private GradingResult calculateGrading(List<Quiz> quizzes, Map<Long, List<Integer>> userAnswers) {
        int total = quizzes.size();
        if (total == 0) return new GradingResult(0.0, 0, 0);

        int correct = 0;

        for (Quiz quiz : quizzes) {
            List<Integer> userAns = userAnswers.getOrDefault(quiz.getId(), Collections.emptyList());
            List<Integer> systemAns = parseJsonToList(quiz.getCorrectAnswers(), Integer.class);

            if (new HashSet<>(userAns).equals(new HashSet<>(systemAns))) {
                correct++;
            }
        }

        int incorrect = total - correct;
        double score = Math.round(((double) correct / total) * 10000.0) / 100.0;

        return new GradingResult(score, correct, incorrect);
    }

    private List<QuizResultSubmission> buildSubmissionDetails(List<Quiz> quizzes, Map<Long, List<Integer>> answers) {
        return quizzes.stream().map(q -> {
            QuizResultSubmission s = new QuizResultSubmission();
            s.setId(q.getId());
            s.setQuestion(q.getQuestion());
            s.setMultipleChoice(q.isMultipleChoice());
            s.setCorrectAnswers(q.getCorrectAnswers());
            s.setOptions(parseJsonToList(q.getOptions(), String.class));
            s.setAnswer(toJson(answers.getOrDefault(q.getId(), Collections.emptyList())));
            return s;
        }).collect(Collectors.toList());
    }

    private <T> List<T> parseJsonToList(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<Long, List<Integer>> parseHistoryJson(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private record GradingResult(double totalScore, int correctCount, int incorrectCount) {}
}
