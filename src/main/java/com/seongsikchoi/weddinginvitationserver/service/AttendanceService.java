package com.seongsikchoi.weddinginvitationserver.service;

import com.seongsikchoi.weddinginvitationserver.dto.AttendanceCreate;
import com.seongsikchoi.weddinginvitationserver.dto.AttendanceJson;
import com.seongsikchoi.weddinginvitationserver.util.JsonFileManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final String JSON_FILE_NAME = "attendance.json";
    
    private final JsonFileManager jsonFileManager;
    
    public List<AttendanceCreate> getAttendance() {
        logger.info(">>> JSON READ: Attendance");
        
        // JSON 파일에서 데이터 읽기
        List<AttendanceJson> attendances = jsonFileManager.readFromFile(JSON_FILE_NAME, AttendanceJson.class);
        
        // DTO로 변환
        List<AttendanceCreate> result = attendances.stream()
                .map(a -> new AttendanceCreate(
                        a.getSide(),
                        a.getName(),
                        a.getMeal(),
                        a.getCount()
                ))
                .collect(Collectors.toList());
        
        logger.info(">>> JSON READ 완료: Attendance - count: {}", result.size());
        return result;
    }
    
    public void createAttendance(AttendanceCreate request) {
        logger.info(">>> JSON WRITE: Attendance - side: {}, name: {}, meal: {}, count: {}", 
                request.getSide(), request.getName(), request.getMeal(), request.getCount());
        
        // JSON 파일에서 기존 데이터 읽기
        List<AttendanceJson> attendances = jsonFileManager.readFromFile(JSON_FILE_NAME, AttendanceJson.class);
        
        // 새 ID 생성 (기존 최대 ID + 1)
        int newId = attendances.stream()
                .mapToInt(a -> a.getId() != null ? a.getId() : 0)
                .max()
                .orElse(0) + 1;
        
        // 새 데이터 생성
        Long timestamp = Instant.now().getEpochSecond();
        
        AttendanceJson newAttendance = new AttendanceJson();
        newAttendance.setId(newId);
        newAttendance.setSide(request.getSide());
        newAttendance.setName(request.getName());
        newAttendance.setMeal(request.getMeal());
        newAttendance.setCount(request.getCount());
        newAttendance.setTimestamp(timestamp);
        
        // 리스트에 추가
        attendances.add(newAttendance);
        
        // JSON 파일에 저장
        jsonFileManager.writeToFile(JSON_FILE_NAME, attendances, AttendanceJson.class);
        
        logger.info(">>> JSON WRITE 완료: Attendance - id: {}, name: {}", newId, request.getName());
    }
}

