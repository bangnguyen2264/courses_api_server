package com.example.course.model.response;

import com.example.course.model.entity.User;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({ "id", "token", "refreshToken", "role" })
public class AuthResponse {
    private UserResponse user;
    private String token;
    private String refreshToken;
    private String role;

    public static AuthResponse from(User user, String token, String refreshToken){
        return AuthResponse.builder()
                .user(UserResponse.toResponse(user))
                .token(token)
                .refreshToken(refreshToken)
                .role(user.getAuthorities().stream().findFirst().get().getAuthority())
                .build();
    }
}
