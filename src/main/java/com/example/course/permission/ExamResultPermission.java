package com.example.course.permission;

import com.example.course.model.entity.ExamResult;
import com.example.course.repository.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamResultPermission implements ResourcePermission {
    private final ExamResultRepository examResultRepository;
    @Override
    public boolean hasPermission(Long userId, Long resourceId) {
        log.info("ExamResultPermission for user: {}, resource: {}", userId, resourceId);
        ExamResult examResult = examResultRepository.findById(resourceId).orElse(null);
        if (examResult == null) {
            log.error("ExamResultPermission denied for user: {}, resource: {}", userId, resourceId);
            return false;
        }
        log.info("ExamResultPermission accept for user: {}, resource: {}", userId, resourceId);
        return userId.equals(examResult.getUser().getId());
    }
}
