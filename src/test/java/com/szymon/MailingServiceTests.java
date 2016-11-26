package com.szymon;

import com.sendgrid.Mail;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.szymon.Texts.RoleEnum;
import com.szymon.Texts.Uri;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import com.szymon.service.mailing.EmailFactory;
import com.szymon.service.mailing.MailingService;
import com.szymon.service.mailing.MailingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

public class MailingServiceTests {

    @Mock
    private SendGrid sendGrid;

    @Mock
    private EmailFactory emailFactory;

    @InjectMocks
    private MailingService mailingService = new MailingServiceImpl();

    private String testTo = "testTo";
    private String testFrom = "testFrom";
    private String testHost = "testHost";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mailingService, "to", testTo);
        ReflectionTestUtils.setField(mailingService, "from", testFrom);
        ReflectionTestUtils.setField(mailingService, "host", testHost);
    }

    @Test
    public void sendActivationCode() throws IOException {
        ActivationCode activationCode = new ActivationCode(null, "test code");
        User user = new User("jankowalski", "Jan", "Kowalski", "Passw0rd", RoleEnum.USER, false);
        Mail testMail = new Mail();
        Request testRequest = new Request();

        Mockito.stub(emailFactory.createMail(testFrom, "Activation " + user.getName()
                + " " + user.getSurname(), testTo, "To activate user " + user.getLogin() + " use this code: "
                + testHost + Uri.AUTH + Uri.ACTIVATE + "?activationCode=" + activationCode.getCode())).toReturn(testMail);
        Mockito.stub(emailFactory.createRequest(testMail)).toReturn(testRequest);

        mailingService.sendActivationCode(activationCode, user);

        Mockito.verify(emailFactory).createMail(testFrom, "Activation " + user.getName()
                + " " + user.getSurname(), testTo, "To activate user " + user.getLogin() + " use this code: "
                + testHost + Uri.AUTH + Uri.ACTIVATE + "?activationCode=" + activationCode.getCode());
        Mockito.verify(emailFactory).createRequest(testMail);
        Mockito.verify(sendGrid).api(testRequest);
    }
}
