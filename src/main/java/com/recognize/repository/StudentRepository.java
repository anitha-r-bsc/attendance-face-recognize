package com.recognize.repository;

import com.recognize.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRegisterNumber(
            String name, String email, String registerNumber
    );
    Optional<Student> findByEmail(String email);
}
