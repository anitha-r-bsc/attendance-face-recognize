package com.recognize.service.impl;

import com.recognize.EmbeddingUtils;
import com.recognize.component.EmbeddingStorage;
import com.recognize.component.FaceDetector;
import com.recognize.component.FaceEmbedding;
import com.recognize.entity.Attendance;
import com.recognize.entity.Student;
import com.recognize.entity.dto.StudentRegisterRequest;
import com.recognize.repository.AttendanceRepository;
import com.recognize.repository.StudentRepository;
import com.recognize.service.FaceRecognitionService;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FaceRecognitionImpl implements FaceRecognitionService {
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final FaceDetector detector;
    private final FaceEmbedding embedder;

    private final Map<String, Mat> knownEmbeddings = EmbeddingStorage.loadEmbeddings();

    @Override
    public Long registerStudent(StudentRegisterRequest request, List<String> studentBase64Image) throws Exception {
        // 1. Check if student exists
        Optional<Student> existingStudent = studentRepository.findByEmail(request.getEmail());
        if (existingStudent.isPresent()) {
            throw new IllegalArgumentException("Student with email " + request.getEmail() + " already exists");
        }

        // 2. Save student in DB
        Student student = Student.fromRequest(request);
        student = studentRepository.save(student);
        Long studentId = student.getId();

        // 3. Save new student's Base64 image to disk
        String newImagePath = saveBase64Image(studentId, request.getName(), studentBase64Image.getFirst());
        File newImageFile = new File(newImagePath);
        if (!newImageFile.exists()) {
            throw new RuntimeException("Failed to save student's image: " + newImagePath);
        }

        Mat img = Imgcodecs.imread(newImageFile.getAbsolutePath());

        // Detect face
        Rect faceRect = detector.detectFace(img);
        if (faceRect == null) {
            throw new IllegalArgumentException("No face detected");
        }

        Mat face = new Mat(img, faceRect);
        Mat embedding = embedder.getEmbedding(face);

        EmbeddingStorage.saveEmbedding(String.valueOf(studentId), embedding);
        knownEmbeddings.put(String.valueOf(studentId), embedding);

        System.out.println("Registered student ID: " + studentId + ", Name: " + request.getName());
        return studentId;
    }

    @Override
    public boolean markAttendance(MultipartFile imageFile) throws Exception {
        String bestId = recognizeFace(imageFile);
        if ("Unknown".equals(bestId)) {
            throw new IllegalArgumentException("No face detected or unknown student");
        }

        Long studentId = Long.parseLong(bestId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalAccessException("student not found"));

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(zoneId);
        ZonedDateTime startOfDay = today.atStartOfDay(zoneId);
        ZonedDateTime endOfDay = today.atTime(23, 59, 59).atZone(zoneId);
        LocalDateTime start = startOfDay.toLocalDateTime();
        LocalDateTime end = endOfDay.toLocalDateTime();

        List<Attendance> hasRecord = attendanceRepository
                .findByStudentIdAndAttendedAtBetween(studentId, start, end);

        if (hasRecord.isEmpty()) {
            throw new IllegalArgumentException("Student (" + hasRecord.getFirst().getStudent().getName() + ") already gave attendance today");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendanceRepository.save(attendance);
        System.out.println("Attendance marked for student ID: " + studentId + " (" + student.getName() + ")");
        return true;
    }

    public String recognizeFace(MultipartFile file) throws Exception {
        // Save uploaded image temporarily
        File temp = File.createTempFile("upload", ".jpg");
        try{
            file.transferTo(temp);

            // Read as Mat
            Mat img = Imgcodecs.imread(temp.getAbsolutePath());

            // Detect face
            Rect faceRect = detector.detectFace(img);
            if (faceRect == null) {
                return "No face detected";
            }

            // Crop face and get embedding
            Mat face = new Mat(img, faceRect);
            Mat embedding = embedder.getEmbedding(face);

            // Compare with stored embeddings
            String bestId = "Unknown";
            double bestSim = 0.0;

            for (var entry : knownEmbeddings.entrySet()) {
                double sim = EmbeddingUtils.cosineSimilarity(embedding, entry.getValue());
                if (sim > bestSim && sim > 0.6) { // threshold
                    bestSim = sim;
                    bestId = entry.getKey();
                }
            }

            System.out.println("Matched: " + bestId + " (similarity: " + bestSim + ")");
            return bestId;
        }finally {
            temp.deleteOnExit();
        }
    }

    public Map<LocalDateTime, String> getMonthlyAttendance(Long studentId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);

        // Convert month start/end to LocalDateTime
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Attendance> presentDays = attendanceRepository.findByStudentIdAndAttendedAtBetween(studentId, start, end);

        Set<LocalDateTime> presentDates = new HashSet<>();
        for (Attendance att : presentDays) {
            // normalize to date only (ignore time)
            presentDates.add(att.getAttendedAt().toLocalDate().atStartOfDay());
        }

        // Build full month map
        Map<LocalDateTime, String> attendanceMap = new LinkedHashMap<>();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDateTime date = ym.atDay(day).atStartOfDay();
            attendanceMap.put(date, presentDates.contains(date) ? "Present" : "Absent");
        }

        return attendanceMap;
    }

    private String saveBase64Image(Long id, String studentName, String base64Image) {
        String[] parts = base64Image.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];

        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        String resourcePath = "faces/" + id;
        File folder = new File("D:/images/" + resourcePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File outputFile = new File(folder, studentName.replace(" ", "_") + ".jpg");
        try (OutputStream os = new FileOutputStream(outputFile)) {
            os.write(imageBytes);
            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}