package com.szymon.service.mailing;

import com.sendgrid.Response;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;

import java.io.IOException;

public interface MailingService {
    Response sendActivationCode (ActivationCode activationCode, User user) throws IOException;
}
