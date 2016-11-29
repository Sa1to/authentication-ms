package com.szymon.service;

import com.szymon.domain.User;
import org.springframework.http.ResponseEntity;

public interface UserAuthService {
    ResponseEntity authenticateUser(String login, String password);

    String createToken(User user);

    ResponseEntity validateAndRemoveToken(String token);
}
