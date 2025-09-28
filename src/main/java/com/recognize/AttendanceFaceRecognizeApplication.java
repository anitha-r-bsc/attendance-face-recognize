package com.recognize;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttendanceFaceRecognizeApplication {

	public static void main(String[] args) {
		System.load("D:/Collage project/attendance- face-recognize/attendance-face-recognize/src/main/resources/opencv_java490.dll");
		SpringApplication.run(AttendanceFaceRecognizeApplication.class, args);
	}

}
