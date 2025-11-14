package com.seongsikchoi.weddinginvitationserver.controller;

import com.seongsikchoi.weddinginvitationserver.dto.AttendanceCreate;
import com.seongsikchoi.weddinginvitationserver.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);
    private final AttendanceService attendanceService;
    
    @GetMapping
    public ResponseEntity<List<AttendanceCreate>> getAttendance() {
        logger.info("=== API 호출: GET /api/attendance ===");
        try {
            List<AttendanceCreate> attendance = attendanceService.getAttendance();
            logger.info("=== API 응답: GET /api/attendance - status: 200, count: {} ===", attendance.size());
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            logger.error("=== API 응답: GET /api/attendance - status: 500, error: {} ===", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Void> createAttendance(@Valid @RequestBody AttendanceCreate request) {
        logger.info("=== API 호출: POST /api/attendance - side: {}, name: {}, meal: {}, count: {} ===", 
                request.getSide(), request.getName(), request.getMeal(), request.getCount());
        try {
            attendanceService.createAttendance(request);
            logger.info("=== API 응답: POST /api/attendance - status: 200, created successfully ===");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("=== API 응답: POST /api/attendance - status: 500, error: {} ===", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

