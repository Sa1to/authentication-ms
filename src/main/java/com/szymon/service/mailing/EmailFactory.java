package com.szymon.service.mailing;

import com.sendgrid.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailFactory {
    public Mail createMail(String from, String subject, String to, String content) {
        return new Mail(new Email(from), subject, new Email(to), new Content("text/plain", content));
    }

    public Request createRequest(Mail mail) {
        Request request = new Request();
        request.method = Method.POST;
        request.endpoint = "mail/send";
        try {
            request.body = mail.build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }
}
