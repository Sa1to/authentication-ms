package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.UserDao;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.RegistrationValidation;
import com.szymon.service.RegistrationValidator;
import com.szymon.service.mailing.MailingService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RegistrationValidatorTests {

    @Mock
    private ActivationCodeService activationCodeService;

    @Mock
    private UserDao userDao;

    @Mock
    private MailingService mailingService;

    @InjectMocks
    private RegistrationValidator registrationValidator = new RegistrationValidation();
    private User userToRegister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userToRegister = new User("testlogin123", "testName", "testSurname", "testPassw0rd", RoleEnum.USER, false);

    }

    @Test
    public void validateCorrectUser() throws IOException {
        ActivationCode activationCode = new ActivationCode();

        Mockito.stub(activationCodeService.createAndSave(userToRegister)).toReturn(activationCode);
        Mockito.stub(userDao.findByLogin(userToRegister.getLogin())).toReturn(null);
        Mockito.stub(mailingService.sendActivationCode(activationCode,userToRegister)).toReturn(new ResponseEntity("test", HttpStatus.OK));

        ResponseEntity responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        Mockito.verify(userDao).findByLogin(userToRegister.getLogin());
        Mockito.verify(userDao).save(userToRegister);
        Mockito.verify(activationCodeService).createAndSave(userToRegister);
        Mockito.verify(mailingService).sendActivationCode(activationCode,userToRegister);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("test", responseEntity.getBody());
    }

    @Test
    public void validateUserWithIncorrectLogin() {
        userToRegister.setLogin("shrt");

        ResponseEntity responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody());

        userToRegister.setLogin(RandomStringUtils.random(21));

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody());

        userToRegister.setLogin("login with space");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody());

        userToRegister.setLogin("");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody());
    }

    @Test
    public void validateUserWithIncorrectPassword() {
        userToRegister.setPassword("shortpa");

        ResponseEntity responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_PASSWORD, responseEntity.getBody());

        userToRegister.setPassword("n0uppercase");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_PASSWORD, responseEntity.getBody());

        userToRegister.setPassword("noNumber");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_PASSWORD, responseEntity.getBody());
    }
}
