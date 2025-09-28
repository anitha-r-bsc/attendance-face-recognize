package com.recognize.component;

import lombok.RequiredArgsConstructor;
import org.opencv.core.*;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FaceDetector {
    private final CascadeClassifier detector;

    public Rect detectFace(Mat img) {
        MatOfRect detections = new MatOfRect();
        detector.detectMultiScale(img, detections);
        Rect[] faces = detections.toArray();
        return (faces.length > 0) ? faces[0] : null;
    }
}
