package com.recognize.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.util.*;

public class EmbeddingStorage {
    private static final String FILE_PATH = "src/main/resources/embeddings.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Mat> loadEmbeddings() {
        try{
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new HashMap<>();
            }

            List<Map<String, Object>> list = mapper.readValue(file, new TypeReference<>() {});
            Map<String, Mat> embeddings = new HashMap<>();

            for (Map<String, Object> entry : list) {
                String id = entry.get("id").toString();
                List<Double> values = (List<Double>) entry.get("embedding");
                Mat vec = new Mat(1, values.size(), CvType.CV_32F);
                for (int i = 0; i < values.size(); i++) {
                    vec.put(0, i, values.get(i));
                }
                embeddings.put(id, vec);
            }

            return embeddings;
        }catch (Exception e){
            throw new RuntimeException("known data not loaded");
        }


    }

    public static void saveEmbedding(String id, Mat embedding) throws Exception {
        // Load existing embeddings
        List<Map<String, Object>> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (file.exists()) {
            list = mapper.readValue(file, new TypeReference<>() {});
        }

        // Convert Mat -> List<Double>
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < embedding.cols(); i++) {
            values.add(embedding.get(0, i)[0]);
        }

        // Remove old entry if exists
        list.removeIf(e -> e.get("id").equals(id));

        // Add new embedding
        Map<String, Object> newEntry = new HashMap<>();
        newEntry.put("id", id);
        newEntry.put("embedding", values);
        list.add(newEntry);

        // Write back to JSON
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
    }
}
