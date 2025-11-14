package com.seongsikchoi.weddinginvitationserver.controller;

import com.seongsikchoi.weddinginvitationserver.dto.*;
import com.seongsikchoi.weddinginvitationserver.service.GuestbookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guestbook")
@RequiredArgsConstructor
public class GuestbookController {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestbookController.class);
    private final GuestbookService guestbookService;
    
    @GetMapping
    public ResponseEntity<GuestbookGetResponse> getGuestbook(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer limit) {
        logger.info("=== API 호출: GET /api/guestbook - offset: {}, limit: {} ===", offset, limit);
        try {
            GuestbookGetResponse response = guestbookService.getGuestbook(offset, limit);
            logger.info("=== API 응답: GET /api/guestbook - status: 200, total: {}, posts count: {} ===", 
                    response.getTotal(), response.getPosts().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("=== API 응답: GET /api/guestbook - status: 500, error: {} ===", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Void> createGuestbookPost(@Valid @RequestBody GuestbookPostForCreate request) {
        logger.info("=== API 호출: POST /api/guestbook - name: {}, content length: {} ===", 
                request.getName(), request.getContent() != null ? request.getContent().length() : 0);
        try {
            guestbookService.createGuestbookPost(request);
            logger.info("=== API 응답: POST /api/guestbook - status: 200, created successfully ===");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("=== API 응답: POST /api/guestbook - status: 500, error: {} ===", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping
    public ResponseEntity<Void> deleteGuestbookPost(@Valid @RequestBody GuestbookPostForDelete request) {
        logger.info("=== API 호출: PUT /api/guestbook (delete) - id: {} ===", request.getId());
        try {
            guestbookService.deleteGuestbookPost(request);
            logger.info("=== API 응답: PUT /api/guestbook (delete) - status: 200, deleted successfully ===");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if ("INCORRECT_PASSWORD".equals(e.getMessage())) {
                logger.warn("=== API 응답: PUT /api/guestbook (delete) - status: 403, incorrect password ===");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            logger.error("=== API 응답: PUT /api/guestbook (delete) - status: 500, error: {} ===", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("=== API 응답: PUT /api/guestbook (delete) - status: 500, error: {} ===", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

