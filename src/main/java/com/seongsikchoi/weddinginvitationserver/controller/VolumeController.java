package com.seongsikchoi.weddinginvitationserver.controller;

import com.seongsikchoi.weddinginvitationserver.util.JsonFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volume")
public class VolumeController {
    
    private static final Logger logger = LoggerFactory.getLogger(VolumeController.class);
    private final JsonFileManager jsonFileManager;
    
    public VolumeController(JsonFileManager jsonFileManager) {
        this.jsonFileManager = jsonFileManager;
    }
    
    /**
     * 볼륨에 있는 JSON 파일 목록 조회
     */
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> listFiles() {
        logger.info("=== API 호출: GET /api/volume/files ===");
        try {
            List<JsonFileManager.FileInfo> files = jsonFileManager.listFiles();
            String dataPath = jsonFileManager.getDataPath();
            
            Map<String, Object> response = new HashMap<>();
            response.put("dataPath", dataPath);
            response.put("fileCount", files.size());
            response.put("files", files);
            
            logger.info("=== API 응답: GET /api/volume/files - status: 200, fileCount: {} ===", files.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("=== API 응답: GET /api/volume/files - status: 500, error: {} ===", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

