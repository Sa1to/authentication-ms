package com.szymon.service;

import com.szymon.domain.User;

public interface ActivationCodeService {
    void createAndSave(User user);
}
