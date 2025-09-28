package com.recognize.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Year;

@Builder
@Data
public class StudentRegisterRequest {
    @NotNull(message = "register number must be not null")
    private String registerNumber; // e.g., enrollment number

    @NotBlank(message = "student name must be not null")
    private String name;

    @NotNull(message = "student dob must be not null")
    private LocalDate dob; // Date of birth

    @NotBlank(message = "student gender must be not null")
    private String gender; // Male/Female/Other

    @NotBlank(message = "student email-address must be not null")
    private String email; // Student email

    @NotBlank(message = "student phone number must be not null")
    private String phoneNumber; // Contact number

    @NotBlank(message = "student address must be not null")
    private String address; // Full address

    @NotBlank(message = "student course must be not null")
    private String course; // e.g., B.Tech, MSc, etc.

    @NotNull(message = "student current year must be not null")
    private Byte year;

    @NotNull(message = "student joined year must be not null")
    private Year joinedYear;

    @NotNull(message = "student image must be not null")
    private String image;
}
