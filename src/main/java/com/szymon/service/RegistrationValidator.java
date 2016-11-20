package com.szymon.service;

import com.szymon.domain.User;
import org.springframework.http.ResponseEntity;

public interface RegistrationValidator {
    ResponseEntity validateUserToRegistration(User user);
}
