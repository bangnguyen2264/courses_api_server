package com.example.course.model.request;

import com.example.course.constant.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
@Data
public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private Gender gender;
    private MultipartFile avatar;
    private LocalDate dob;
    private String address;
}
