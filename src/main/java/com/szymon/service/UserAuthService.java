package com.szymon.service;

import com.auth0.jwt.internal.com.fasterxml.jackson.core.JsonToken;
import com.szymon.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserAuthService {
    ResponseEntity authenticateUser(String login, String password);

    String createToken(User user);
}
