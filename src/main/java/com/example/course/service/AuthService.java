package com.example.course.service;

import com.example.course.model.request.LoginRequest;
import com.example.course.model.request.RegisterRequest;
import com.example.course.model.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);
    AuthResponse refresh(String refreshToken);

}
