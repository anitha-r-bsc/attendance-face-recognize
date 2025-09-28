package com.recognize.service;

import com.recognize.entity.dto.StudentRegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FaceRecognitionService {
    Long registerStudent(StudentRegisterRequest request, List<String> studentBase64Image) throws Exception;
    boolean markAttendance(MultipartFile imageFile) throws Exception;
    Map<LocalDateTime, String> getMonthlyAttendance(Long studentId, int month, int year);
}
