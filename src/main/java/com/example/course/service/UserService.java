package com.example.course.service;

import com.example.course.filter.UserFilter;
import com.example.course.model.request.ChangePasswordRequest;
import com.example.course.model.request.UserUpdateRequest;
import com.example.course.model.response.ApiResponse;
import com.example.course.model.response.UserResponse;

public interface UserService {

    ApiResponse<UserResponse> getAll(UserFilter filter);
    UserResponse getById(Long id);
    UserResponse update(Long id, UserUpdateRequest request);
    void delete(Long id);
    String changePassword(Long id, ChangePasswordRequest request);
}
