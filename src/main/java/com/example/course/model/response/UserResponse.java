package com.example.course.model.response;

import com.example.course.constant.Gender;
import com.example.course.model.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private String avatarUrl;
    private LocalDate dob;
    private String address;

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .dob(user.getDob())
                .address(user.getAddress())
                .build();
    }
}
