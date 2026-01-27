package com.example.course.security.author;

import java.lang.annotation.*;

// QUAN TRỌNG: Phải khớp target với RequirePermission
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermissions {
    RequirePermission[] value();
}