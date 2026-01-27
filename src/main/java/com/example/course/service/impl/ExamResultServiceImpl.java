package com.example.course.service.impl;

import com.example.course.exception.NotFoundException;
import com.example.course.filter.ExamResultFilter;
import com.example.course.model.entity.Exam;
import com.example.course.model.entity.ExamResult;
import com.example.course.model.entity.Quiz;
import com.example.course.model.entity.User;
import com.example.course.model.request.ExamSubmitRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.ExamResultDetailResponse;
import com.example.course.model.response.ExamResultResponse;
import com.example.course.model.response.QuizResultSubmission;
import com.example.course.repository.ExamRepository;
import com.example.course.repository.ExamResultRepository;
import com.example.course.repository.QuizRepository; // Import mới
import com.example.course.repository.UserRepository;
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
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @CacheEvict(value = "exam-result-list", allEntries = true)
    public ExamResultDetailResponse submit(ExamSubmitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found id: " + request.getUserId()));

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new NotFoundException("Exam not found id: " + request.getExamId()));

        List<Quiz> quizzes = quizRepository.findByExams_Id(request.getExamId());
        Map<Long, List<Integer>> safeAnswers = request.getAnswers() != null ? request.getAnswers() : new HashMap<>();
        GradingResult grading = calculateGrading(quizzes, safeAnswers);

        ExamResult result = ExamResult.builder()
                .user(user)
                .exam(exam)
                .score(grading.totalScore)
                .correct(grading.correctCount)
                .incorrect(grading.incorrectCount)
                .timeTaken(request.getTimeTaken())
                .submissionHistory(toJson(safeAnswers))
                .build();
        examResultRepository.save(result);

        List<QuizResultSubmission> details = buildSubmissionDetails(quizzes, safeAnswers);

        return ExamResultDetailResponse.toResponse(result, details);
    }

    @Override
    @Cacheable(value = "exam-result", key = "#id")
    public ExamResultDetailResponse getById(Long id) {
        ExamResult result = examResultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Result not found"));

        Map<Long, List<Integer>> userAnswersMap = parseHistoryJson(result.getSubmissionHistory());
        List<Quiz> quizzes = quizRepository.findByExams_Id(result.getExam().getId());
        List<QuizResultSubmission> details = buildSubmissionDetails(quizzes, userAnswersMap);

        return ExamResultDetailResponse.toResponse(result, details);
    }

    @Override
    @Cacheable(
            value = "exam-result-list",
            key = "T(java.util.Objects).hash(#filter)"
    )
    public ApiResponse<ExamResultResponse> getAll(ExamResultFilter filter) {
        Pageable pageable = PageableUtil.createPageable(filter);
        Page<ExamResult> page = examResultRepository.findAll(ExamResultSpecification.filter(filter), pageable);

        return ApiResponse.fromPage(page.map(ExamResultResponse::toResponse));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "exam-result", key = "#id"),
                    @CacheEvict(value = "exam-result-list", allEntries = true)
            }
    )
    public void delete(Long id) {
        if (!examResultRepository.existsById(id)) {
            throw new NotFoundException("Result not found id: " + id);
        }
        examResultRepository.deleteById(id);
    }

    private GradingResult calculateGrading(List<Quiz> quizzes, Map<Long, List<Integer>> userAnswers) {
        int totalQuestions = quizzes.size();
        if (totalQuestions == 0) return new GradingResult(0.0, 0, 0);

        int correctCount = 0;
        for (Quiz quiz : quizzes) {
            List<Integer> userAns = userAnswers.getOrDefault(quiz.getId(), Collections.emptyList());
            List<Integer> systemAns = parseJsonToList(quiz.getCorrectAnswers(), Integer.class);

            if (isAnswerCorrect(userAns, systemAns)) {
                correctCount++;
            }
        }

        int incorrectCount = totalQuestions - correctCount;
        double score = Math.round(((double) correctCount / totalQuestions) * 100.0 * 100.0) / 100.0;

        return new GradingResult(score, correctCount, incorrectCount);
    }

    private List<QuizResultSubmission> buildSubmissionDetails(List<Quiz> quizzes, Map<Long, List<Integer>> userAnswersMap) {
        return quizzes.stream().map(quiz -> {
            QuizResultSubmission submission = new QuizResultSubmission();
            submission.setId(quiz.getId());
            submission.setQuestion(quiz.getQuestion());
            submission.setMultipleChoice(quiz.isMultipleChoice());
            submission.setCorrectAnswers(quiz.getCorrectAnswers());
            submission.setOptions(parseJsonToList(quiz.getOptions(), String.class));
            List<Integer> userAnsList = userAnswersMap.getOrDefault(quiz.getId(), Collections.emptyList());
            submission.setAnswer(toJson(userAnsList));
            return submission;
        }).collect(Collectors.toList());
    }

    private boolean isAnswerCorrect(List<Integer> userAns, List<Integer> systemAns) {
        if (userAns == null || systemAns == null) return false;
        return new HashSet<>(userAns).equals(new HashSet<>(systemAns));
    }

    private <T> List<T> parseJsonToList(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            log.warn("Error parsing JSON to List<{}>: {}", clazz.getSimpleName(), json);
            return Collections.emptyList();
        }
    }

    private Map<Long, List<Integer>> parseHistoryJson(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<Long, List<Integer>>>() {});
        } catch (Exception e) {
            log.error("Error parsing history json", e);
            return new HashMap<>();
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON write error", e);
            return "[]";
        }
    }

    private record GradingResult(double totalScore, int correctCount, int incorrectCount) {}
}