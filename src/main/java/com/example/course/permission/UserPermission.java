package com.example.course.permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPermission implements ResourcePermission {

    @Override
    public boolean hasPermission(Long userId, Long resourceId) {
        log.info("UserPermission hasPermission userId: {}, resourceId: {}", userId, resourceId);
        return userId.equals(resourceId);
    }
}
