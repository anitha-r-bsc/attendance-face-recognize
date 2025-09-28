package com.recognize.repository;

import com.recognize.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdAndAttendedAtBetween(Long studentId, LocalDateTime start, LocalDateTime end);
}
