package com.szymon.service.mailing;

import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface MailingService {
    ResponseEntity sendActivationCode (ActivationCode activationCode, User user) throws IOException;
}
