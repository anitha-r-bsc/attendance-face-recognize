package com.recognize.component;

import org.opencv.objdetect.CascadeClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCvConfig {

    @Bean
    public CascadeClassifier cascadeClassifier() {
        CascadeClassifier classifier = new CascadeClassifier("src/main/resources/haarcascade_frontalface_alt.xml");
        if (classifier.empty()) {
            throw new RuntimeException("Failed to load classifier");
        }
        return classifier;
    }
}
