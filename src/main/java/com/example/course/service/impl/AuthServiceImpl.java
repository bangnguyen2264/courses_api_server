package com.example.course.service.impl;

import com.example.course.exception.InvalidRefreshTokenException;
import com.example.course.mapper.UserMapper;
import com.example.course.model.entity.Role;
import com.example.course.model.entity.User;
import com.example.course.model.request.LoginRequest;
import com.example.course.model.request.RegisterRequest;
import com.example.course.model.response.AuthResponse;
import com.example.course.repository.RoleRepository;
import com.example.course.repository.UserRepository;
import com.example.course.security.authen.JwtService;
import com.example.course.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + loginRequest.getEmail()));

        log.info("User {} logged in", user.getUsername());
        return AuthResponse.from(user, accessToken, refreshToken);
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail((registerRequest.getEmail()))) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Not found role ROLE_USER"));

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);

        userRepository.save(user);
        log.info("User {} registered", user.getUsername());
        return "Success register new user";
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (refreshToken != null) {
            refreshToken = refreshToken.replaceFirst("Bearer ", "");
            if (jwtService.validateRefreshToken(refreshToken)) {
                Authentication auth = jwtService.createAuthentication(refreshToken);

                User user = userRepository.findByEmail(auth.getName())
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + auth.getName()));

                log.info("User {} refreshed token", user.getUsername());
                return AuthResponse.from(user, jwtService.generateAccessToken(auth), refreshToken);
            }
        }
        throw new InvalidRefreshTokenException(refreshToken);    }
}
