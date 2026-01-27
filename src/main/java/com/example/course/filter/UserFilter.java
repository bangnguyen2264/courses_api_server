package com.example.course.filter;

import com.example.course.constant.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserFilter extends BaseFilter {

    @Schema(description = "Search by full name, email or phone number")
    private String search;

    @Schema(description = "Filter by gender")
    private Gender gender;

    @Schema(description = "Filter by role name (ROLE_USER, ROLE_ADMIN...)")
    private String role;

    @Schema(description = "Date of birth from")
    @PastOrPresent
    private LocalDate dobFrom;

    @Schema(description = "Date of birth to")
    @Past
    private LocalDate dobTo;
}
