package com.example.course.controller;

import com.example.course.filter.UserFilter;
import com.example.course.model.request.ChangePasswordRequest;
import com.example.course.model.request.UserUpdateRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.UserResponse;
import com.example.course.permission.UserPermission;
import com.example.course.security.author.RequirePermission;
import com.example.course.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getAll(@Valid @ParameterObject UserFilter filter) {
        ApiResponse<UserResponse> response = userService.getAll(filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequirePermission(resource = UserPermission.class, idParam = "#id")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission(resource = UserPermission.class, idParam = "#id")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @ModelAttribute UserUpdateRequest request
    ) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(resource = UserPermission.class, idParam = "#id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PutMapping("/{id}/change-password")
    @RequirePermission(resource = UserPermission.class, idParam = "#id")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String message = userService.changePassword(id, request);
        return ResponseEntity.ok(message);
    }
}
