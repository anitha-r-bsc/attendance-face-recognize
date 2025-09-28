package com.recognize.controller;

import com.recognize.entity.Student;
import com.recognize.repository.StudentRepository;
import com.recognize.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @GetMapping
    public String listStudents(@RequestParam(required = false) String keyword, Model model) {
        List<Student> students;
        if (keyword != null && !keyword.isEmpty()) {
            students = studentRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRegisterNumber(
                    keyword, keyword, keyword
            );
        } else {
            students = studentRepository.findAll();
        }
        LocalDate today = LocalDate.now(TimeZone.getTimeZone("Asia/Kolkata").toZoneId());
        model.addAttribute("currentMonth", today.getMonthValue());
        model.addAttribute("currentYear", today.getYear());
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        return "students"; // students.html
    }

    @GetMapping("/student-attendance")
    public String studentAttendance(
            @RequestParam Long studentId,
            @RequestParam int month,
            @RequestParam int year,
            Model model
    ) {
        Map<LocalDateTime, String> attendance = faceRecognitionService.getMonthlyAttendance(studentId, month, year);
        model.addAttribute("attendance", attendance);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("studentId", studentId);
        return "student-attendance";
    }
}
