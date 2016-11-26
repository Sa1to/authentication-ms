package com.szymon.service;

import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import org.springframework.http.ResponseEntity;

public interface ActivationCodeService {
    ActivationCode createAndSave(User user);

    ResponseEntity activateUser(String activationCode);
}
