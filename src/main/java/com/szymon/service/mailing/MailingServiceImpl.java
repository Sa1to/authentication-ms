package com.szymon.service.mailing;

import com.sendgrid.*;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MailingServiceImpl implements MailingService {

    @Autowired
    private EmailFactory emailFactory;

    @Autowired
    private SendGrid sendGrid;

    @Value("${mail.send.to}")
    String to;

    @Value("${mail.send.from}")
    String from;

    @Override
    public Response sendActivationCode(ActivationCode activationCode, User user) {
        Mail mail = emailFactory.createMail(from, "Activation " + user.getName()
                + " " + user.getSurname(), to,"To activate user " + user.getLogin() + "user this code: " + activationCode.getCode());

        try {
            return sendGrid.api(emailFactory.createRequest(mail));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
