package com.seongsikchoi.weddinginvitationserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonFileManager {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonFileManager.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${json.data.path:./data}")
    private String dataPath;
    
    /**
     * JSON 파일에서 데이터 읽기
     */
    public <T> List<T> readFromFile(String fileName, Class<T> clazz) {
        String filePath = getFilePath(fileName);
        File file = new File(filePath);
        
        if (!file.exists()) {
            logger.info(">>> JSON file not found, creating empty file: {}", filePath);
            ensureDirectoryExists();
            writeToFile(fileName, new ArrayList<>(), clazz);
            return new ArrayList<>();
        }
        
        try {
            List<T> data = objectMapper.readValue(file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            logger.info(">>> Read {} records from {}", data.size(), fileName);
            return data;
        } catch (IOException e) {
            logger.error(">>> Error reading JSON file: {}", filePath, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * JSON 파일에 데이터 쓰기
     */
    public <T> void writeToFile(String fileName, List<T> data, Class<T> clazz) {
        String filePath = getFilePath(fileName);
        ensureDirectoryExists();
        
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
            logger.info(">>> Wrote {} records to {}", data.size(), fileName);
        } catch (IOException e) {
            logger.error(">>> Error writing JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }
    
    /**
     * 파일 경로 생성
     */
    private String getFilePath(String fileName) {
        return dataPath + File.separator + fileName;
    }
    
    /**
     * 데이터 디렉토리 존재 확인 및 생성
     */
    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(dataPath));
            logger.debug(">>> Data directory ensured: {}", dataPath);
        } catch (IOException e) {
            logger.error(">>> Error creating data directory: {}", dataPath, e);
            throw new RuntimeException("Failed to create data directory: " + dataPath, e);
        }
    }
}

