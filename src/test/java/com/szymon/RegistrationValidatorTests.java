package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.UserDao;
import com.szymon.domain.User;
import com.szymon.service.RegistrationCodeService;
import com.szymon.service.RegistrationValidation;
import com.szymon.service.RegistrationValidator;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class RegistrationValidatorTests {

    @Mock
    private RegistrationCodeService registrationCodeService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private RegistrationValidator registrationValidator = new RegistrationValidation();
    private User userToRegister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userToRegister = new User("testlogin123", "testName", "testSurname", "testPassw0rd", RoleEnum.USER, false);

    }

    @Test
    public void validateCorrectUser() {
        Mockito.stub(userDao.findByLogin(userToRegister.getLogin())).toReturn(null);
        ResponseEntity responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        Mockito.verify(userDao).findByLogin(userToRegister.getLogin());
        Mockito.verify(registrationCodeService).createAndSave(userToRegister);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void validateUserWithIncorrectLogin() {
        userToRegister.setLogin("shrt");

        ResponseEntity responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody().toString());

        userToRegister.setLogin(RandomStringUtils.random(21));

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody().toString());

        userToRegister.setLogin("login with space");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody().toString());

        userToRegister.setLogin("");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_LOGIN, responseEntity.getBody().toString());
    }

    @Test
    public void validateUserWithIncorrectPassword() {
        userToRegister.setPassword("shortpa");

        ResponseEntity responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_PASSWORD, responseEntity.getBody().toString());

        userToRegister.setPassword("n0uppercase");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_PASSWORD, responseEntity.getBody().toString());

        userToRegister.setPassword("noNumber");

        responseEntity = registrationValidator.validateUserToRegistration(userToRegister);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_PASSWORD, responseEntity.getBody().toString());
    }
}
