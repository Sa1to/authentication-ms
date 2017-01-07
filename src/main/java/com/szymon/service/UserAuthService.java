package com.szymon.service;

import com.szymon.domain.User;
import org.springframework.http.ResponseEntity;

public interface UserAuthService {
    ResponseEntity authenticateUserBaseOnCredentials(String login, String password);

    ResponseEntity authenticateUserBaseOnToken(String token);

    String createAndSaveToken(User user);

}
