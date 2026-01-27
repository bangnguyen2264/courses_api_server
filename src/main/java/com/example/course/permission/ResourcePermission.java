package com.example.course.permission;

public interface ResourcePermission {
    boolean hasPermission(Long userId, Long resourceId);
}

