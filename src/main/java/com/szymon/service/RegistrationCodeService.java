package com.szymon.service;

import com.szymon.domain.User;

public interface RegistrationCodeService {
    void createAndSave(User user);
}
