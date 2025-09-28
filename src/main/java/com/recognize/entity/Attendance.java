package com.recognize.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Entity
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many attendance records belong to one student
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false) // FK column in Attendance table
    private Student student;

    @Column(name = "attended_at", nullable = false)
    private LocalDateTime attendedAt;

    @PrePersist
    public void beforeInsert(){
        this.attendedAt = LocalDateTime.now(TimeZone.getTimeZone("Asia/Kolkata").toZoneId());
    }
}
