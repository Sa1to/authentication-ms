package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.controller.UserController;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.RegistrationValidator;
import com.szymon.service.UserAuthService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class ControllerTests {

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private RegistrationValidator registrationValidator;

    @Mock
    private ActivationCodeService activationCodeService;

    @InjectMocks
    private UserController userController = new UserController();

    private String testCorrectLogin = "testLogin";
    private String testCorrectPassword = "testPassword";

    private ResponseEntity wrongCredentials = new ResponseEntity<>(Responses.WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
    private ResponseEntity inactiveUser = new ResponseEntity<>(Responses.INACTIVE_USER, HttpStatus.BAD_REQUEST);
    private ResponseEntity correct = new ResponseEntity<>("token", HttpStatus.OK);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendCorrectCredentials() {

        Mockito.stub(userAuthService.authenticateUser(testCorrectLogin, testCorrectPassword)).toReturn(correct);

        ResponseEntity responseEntity = userController.loginUser(testCorrectLogin, testCorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testCorrectLogin, testCorrectPassword);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(correct.getBody(), responseEntity.getBody());
    }

    @Test
    public void sendWrongCredentials() {
        String testIncorrectPassword = "wrong";
        Mockito.stub(userAuthService.authenticateUser(testCorrectLogin, testIncorrectPassword)).toReturn(wrongCredentials);

        ResponseEntity responseEntity = userController.loginUser(testCorrectLogin, testIncorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testCorrectLogin, testIncorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody().toString());

        String testIncorrectLogin = "wrong";
        Mockito.stub(userAuthService.authenticateUser(testIncorrectLogin, testCorrectPassword)).toReturn(wrongCredentials);

        responseEntity = userController.loginUser(testIncorrectLogin, testCorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testIncorrectLogin, testCorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody().toString());
    }

    @Test
    public void loginAsInactiveUser() {
        Mockito.stub(userAuthService.authenticateUser(testCorrectLogin, testCorrectPassword)).toReturn(inactiveUser);

        ResponseEntity responseEntity = userController.loginUser(testCorrectLogin, testCorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testCorrectLogin, testCorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INACTIVE_USER, responseEntity.getBody().toString());
    }

    @Test
    public void registerUser() {
        String activationCode = "activationCode";
        User userToRegister = new User("testlogin123", "testName", "testSurname", "testPassword123", RoleEnum.USER, false);

        Mockito.stub(registrationValidator.validateUserToRegistration(userToRegister)).toReturn(new ResponseEntity<>(activationCode, HttpStatus.OK));

        ResponseEntity responseEntity = userController.registerUser(userToRegister);

        Mockito.verify(registrationValidator).validateUserToRegistration(userToRegister);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(activationCode, responseEntity.getBody().toString());
    }

    @Test
    public void activateUser() {
        String activationCode = "activationCode";
        ResponseEntity expectedResponse = new ResponseEntity<>(Responses.USER_ACTIVATED, HttpStatus.OK);

        Mockito.stub(activationCodeService.activateUser(activationCode)).toReturn(expectedResponse);

        ResponseEntity responseEntity = userController.activateUser(activationCode);

        Mockito.verify(activationCodeService).activateUser(activationCode);
        assertEquals(expectedResponse, responseEntity);
    }

}
