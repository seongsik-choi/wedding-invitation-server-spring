package com.seongsikchoi.weddinginvitationserver.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(14);
    
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    public boolean checkPasswordHash(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }
}

