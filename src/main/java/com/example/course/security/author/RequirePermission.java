package com.example.course.security.author;

import com.example.course.permission.ResourcePermission;
import java.lang.annotation.*;

@Target(ElementType.METHOD) // Khớp với cái trên
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequirePermissions.class)
public @interface RequirePermission {
    Class<? extends ResourcePermission> resource();
    String idParam();
}