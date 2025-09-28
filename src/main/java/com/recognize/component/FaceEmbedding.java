package com.recognize.component;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.springframework.stereotype.Service;

@Service
public class FaceEmbedding {
    private Net net;

    public FaceEmbedding() {
        this.net = Dnn.readNetFromTorch("src/main/resources/nn4.small2.v1.t7"); // OpenFace model
    }

    public Mat getEmbedding(Mat face) {
        Mat blob = Dnn.blobFromImage(face, 1.0/255.0, new Size(96, 96), new Scalar(0,0,0), true, false);
        net.setInput(blob);
        return net.forward().clone(); // 128D embedding
    }
}

