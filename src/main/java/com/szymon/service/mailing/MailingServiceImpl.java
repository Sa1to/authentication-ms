package com.szymon.service.mailing;

import com.sendgrid.*;
import com.szymon.Texts.Responses;
import com.szymon.Texts.Uri;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Value("${host.address}")
    String host;

    @Override
    public ResponseEntity sendActivationCode(ActivationCode activationCode, User user) {
        Mail mail = emailFactory.createMail(from, "Activation " + user.getName()
                + " " + user.getSurname(), to, "To activate user " + user.getLogin() + " use this code: "
                + host + Uri.AUTH + Uri.ACTIVATE + "?activationCode=" + activationCode.getCode());

        try {
            sendGrid.api(emailFactory.createRequest(mail));
            return new ResponseEntity<>(Responses.ACTIVATION_CODE_SENT, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
