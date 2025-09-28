package com.recognize;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class EmbeddingUtils {
    public static double cosineSimilarity(Mat v1, Mat v2) {
        double dot = v1.dot(v2);
        double norm1 = Core.norm(v1);
        double norm2 = Core.norm(v2);
        return dot / (norm1 * norm2);
    }
}

