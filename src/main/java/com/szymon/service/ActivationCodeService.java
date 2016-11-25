package com.szymon.service;

import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;

public interface ActivationCodeService {
    ActivationCode createAndSave(User user);
}
