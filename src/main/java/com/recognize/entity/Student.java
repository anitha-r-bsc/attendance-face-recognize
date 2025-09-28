package com.recognize.entity;

import com.recognize.entity.dto.StudentRegisterRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.TimeZone;

@Entity
@Data
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated unique ID (used as label in model)

    @Column(name = "register_number")
    private String registerNumber; // e.g., enrollment number

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dob; // Date of birth

    @Column(name = "gender", nullable = false)
    private String gender; // Male/Female/Other

    @Column(name = "email", nullable = false)
    private String email; // Student email

    @Column(name = "phone_number")
    private String phoneNumber; // Contact number

    @Column(name = "address", nullable = false)
    private String address; // Full address

    @Column(name = "course", nullable = false)
    private String course; // e.g., B.Tech, MSc, etc.

    @Column(name = "year", nullable = false)
    private Byte year; // Current study year (1, 2, 3, 4)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "joined_year", nullable = false)
    private Year joinedYear;

    @PrePersist
    public void beforeInsert(){
        this.createdAt = LocalDateTime.now(TimeZone.getTimeZone("Asia/Kolkata").toZoneId());
    }

    public static Student fromRequest(StudentRegisterRequest request){
        Student student = new Student();
        student.setRegisterNumber(request.getRegisterNumber());
        student.setName(request.getName());
        student.setDob(request.getDob());
        student.setGender(request.getGender());
        student.setEmail(request.getEmail());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setAddress(request.getAddress());
        student.setCourse(request.getCourse());
        student.setYear(request.getYear());
        student.setJoinedYear(request.getJoinedYear());

        return student;
    }
}

