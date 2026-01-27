package com.example.course.model.request;

import com.example.course.constant.Gender;
import com.example.course.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dob;
    private String address;

    public static User toEntity(RegisterRequest registerRequest) {
        return User.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .gender(registerRequest.getGender())
                .phoneNumber(registerRequest.getPhoneNumber())
                .password(registerRequest.getPassword())
                .dob(registerRequest.getDob())
                .address(registerRequest.getAddress())
                .build();

    }
}
