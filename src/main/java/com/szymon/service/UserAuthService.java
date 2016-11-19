package com.szymon.service;

import com.szymon.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserAuthService {
    ResponseEntity authenticateUser(String login, String password);

    String createToken(User user);
}
