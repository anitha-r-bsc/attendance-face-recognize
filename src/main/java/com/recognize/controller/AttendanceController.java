package com.recognize.controller;

import com.recognize.entity.dto.StudentRegisterRequest;
import com.recognize.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegisterRequest request) {
        try {
            String base64Image = request.getImage();
            faceRecognitionService.registerStudent(
                    request,
                    List.of(base64Image)
            );

            // Return JSON instead of plain text
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "New student registered successfully"
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
           ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Something went wrong"));
        }
    }


    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestParam("image") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("success", false, "message", "Sorry we could not recognize your face"));
            }

            boolean marked = faceRecognitionService.markAttendance(imageFile);
            if (marked) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Attendance marked successfully"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message", "Unknown student or no face detected"));
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("success", false, "message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Something went wrong"));
        }
    }
}