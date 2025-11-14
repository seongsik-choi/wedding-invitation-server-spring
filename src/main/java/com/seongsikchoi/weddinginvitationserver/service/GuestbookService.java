package com.seongsikchoi.weddinginvitationserver.service;

import com.seongsikchoi.weddinginvitationserver.dto.*;
import com.seongsikchoi.weddinginvitationserver.util.JsonFileManager;
import com.seongsikchoi.weddinginvitationserver.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    
    private static final Logger logger = LoggerFactory.getLogger(GuestbookService.class);
    private static final String JSON_FILE_NAME = "guestbook.json";
    
    private final JsonFileManager jsonFileManager;
    private final PasswordUtil passwordUtil;
    
    @Value("${admin.password}")
    private String adminPassword;
    
    public GuestbookGetResponse getGuestbook(Integer offset, Integer limit) {
        logger.info(">>> JSON READ: Guestbook - offset: {}, limit: {}", offset, limit);
        
        // JSON 파일에서 데이터 읽기
        List<GuestbookJson> allGuestbooks = jsonFileManager.readFromFile(JSON_FILE_NAME, GuestbookJson.class);
        
        // 유효한 데이터만 필터링하고 시간순 정렬
        List<GuestbookJson> validGuestbooks = allGuestbooks.stream()
                .filter(g -> g.getValid() != null && g.getValid())
                .sorted(Comparator.comparing(GuestbookJson::getTimestamp).reversed())
                .collect(Collectors.toList());
        
        int total = validGuestbooks.size();
        
        // 페이징 처리
        int start = Math.min(offset, total);
        int end = Math.min(start + limit, total);
        List<GuestbookJson> pagedGuestbooks = validGuestbooks.subList(start, end);
        
        // DTO로 변환
        List<GuestbookPostForGet> posts = pagedGuestbooks.stream()
                .map(g -> new GuestbookPostForGet(
                        g.getId(),
                        g.getName(),
                        g.getContent(),
                        g.getTimestamp()
                ))
                .collect(Collectors.toList());
        
        logger.info(">>> JSON READ 완료: Guestbook - total: {}, returned: {}", total, posts.size());
        return new GuestbookGetResponse(posts, total);
    }
    
    public void createGuestbookPost(GuestbookPostForCreate request) {
        logger.info(">>> JSON WRITE: Guestbook - name: {}", request.getName());
        
        // JSON 파일에서 기존 데이터 읽기
        List<GuestbookJson> guestbooks = jsonFileManager.readFromFile(JSON_FILE_NAME, GuestbookJson.class);
        
        // 새 ID 생성 (기존 최대 ID + 1)
        int newId = guestbooks.stream()
                .mapToInt(g -> g.getId() != null ? g.getId() : 0)
                .max()
                .orElse(0) + 1;
        
        // 새 데이터 생성
        String hashedPassword = passwordUtil.hashPassword(request.getPassword());
        Long timestamp = Instant.now().getEpochSecond();
        
        GuestbookJson newGuestbook = new GuestbookJson();
        newGuestbook.setId(newId);
        newGuestbook.setName(request.getName());
        newGuestbook.setContent(request.getContent());
        newGuestbook.setPassword(hashedPassword);
        newGuestbook.setTimestamp(timestamp);
        newGuestbook.setValid(true);
        
        // 리스트에 추가
        guestbooks.add(newGuestbook);
        
        // JSON 파일에 저장
        jsonFileManager.writeToFile(JSON_FILE_NAME, guestbooks, GuestbookJson.class);
        
        logger.info(">>> JSON WRITE 완료: Guestbook - id: {}, name: {}", newId, request.getName());
    }
    
    public void deleteGuestbookPost(GuestbookPostForDelete request) {
        logger.info(">>> JSON UPDATE: Guestbook (soft delete) - id: {}", request.getId());
        
        // JSON 파일에서 데이터 읽기
        List<GuestbookJson> guestbooks = jsonFileManager.readFromFile(JSON_FILE_NAME, GuestbookJson.class);
        
        // 해당 ID의 게스트북 찾기
        GuestbookJson guestbook = guestbooks.stream()
                .filter(g -> g.getId() != null && g.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("NO_GUESTBOOK_POST_FOUND"));
        
        if (guestbook.getValid() == null || !guestbook.getValid()) {
            throw new RuntimeException("NO_GUESTBOOK_POST_FOUND");
        }
        
        boolean passwordMatch = false;
        
        // 관리자 비밀번호 확인
        if (adminPassword != null && !adminPassword.isEmpty() && adminPassword.equals(request.getPassword())) {
            passwordMatch = true;
        } else {
            // 일반 비밀번호 확인
            passwordMatch = passwordUtil.checkPasswordHash(request.getPassword(), guestbook.getPassword());
        }
        
        if (!passwordMatch) {
            throw new RuntimeException("INCORRECT_PASSWORD");
        }
        
        // soft delete (valid를 false로 변경)
        guestbook.setValid(false);
        
        // JSON 파일에 저장
        jsonFileManager.writeToFile(JSON_FILE_NAME, guestbooks, GuestbookJson.class);
        
        logger.info(">>> JSON UPDATE 완료: Guestbook - id: {}, valid: {}", guestbook.getId(), guestbook.getValid());
    }
}

