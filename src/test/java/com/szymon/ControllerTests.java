package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.controller.UserController;
import com.szymon.domain.Credentials;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.RegistrationValidator;
import com.szymon.service.TokenRenewService;
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

    @Mock
    private TokenRenewService tokenRenewService;

    @InjectMocks
    private UserController userController = new UserController();

    private String testCorrectLogin = "testLogin";
    private String testCorrectPassword = "testPassword";
    private Credentials credentials = new Credentials(testCorrectLogin, testCorrectPassword);

    private ResponseEntity wrongCredentials = new ResponseEntity<>(Responses.WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
    private ResponseEntity inactiveUser = new ResponseEntity<>(Responses.INACTIVE_USER, HttpStatus.BAD_REQUEST);
    private ResponseEntity correct = new ResponseEntity<>("token", HttpStatus.OK);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendCorrectCredentials() {

        Mockito.stub(userAuthService.authenticateUserBaseOnCredentials(testCorrectLogin, testCorrectPassword)).toReturn(correct);

        ResponseEntity responseEntity = userController.loginUser(credentials);

        Mockito.verify(userAuthService).authenticateUserBaseOnCredentials(testCorrectLogin, testCorrectPassword);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(correct.getBody(), responseEntity.getBody());
    }

    @Test
    public void sendWrongCredentials() {
        String testIncorrectPassword = "wrong";
        credentials.setPassword(testIncorrectPassword);
        Mockito.stub(userAuthService.authenticateUserBaseOnCredentials(testCorrectLogin, testIncorrectPassword)).toReturn(wrongCredentials);

        ResponseEntity responseEntity = userController.loginUser(credentials);

        Mockito.verify(userAuthService).authenticateUserBaseOnCredentials(testCorrectLogin, testIncorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody());

        String testIncorrectLogin = "wrong";
        credentials.setPassword(testCorrectPassword);
        credentials.setLogin(testIncorrectLogin);
        Mockito.stub(userAuthService.authenticateUserBaseOnCredentials(testIncorrectLogin, testCorrectPassword)).toReturn(wrongCredentials);

        responseEntity = userController.loginUser(credentials);

        Mockito.verify(userAuthService).authenticateUserBaseOnCredentials(testIncorrectLogin, testCorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody());
    }

    @Test
    public void loginAsInactiveUser() {
        Mockito.stub(userAuthService.authenticateUserBaseOnCredentials(testCorrectLogin, testCorrectPassword)).toReturn(inactiveUser);

        ResponseEntity responseEntity = userController.loginUser(credentials);

        Mockito.verify(userAuthService).authenticateUserBaseOnCredentials(testCorrectLogin, testCorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.INACTIVE_USER, responseEntity.getBody());
    }

    @Test
    public void registerUser() {
        String activationCode = "activationCode";
        User userToRegister = new User("testlogin123", "testName", "testSurname", "testPassword123", RoleEnum.USER, false);

        Mockito.stub(registrationValidator.validateUserToRegistration(userToRegister)).toReturn(new ResponseEntity<>(activationCode, HttpStatus.OK));

        ResponseEntity responseEntity = userController.registerUser(userToRegister);

        Mockito.verify(registrationValidator).validateUserToRegistration(userToRegister);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(activationCode, responseEntity.getBody());
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

    @Test
    public void authenticateUser() {
        String token = "testToken";
        ResponseEntity expectedResponse = new ResponseEntity<>(HttpStatus.OK);
        Mockito.stub(userAuthService.authenticateUserBaseOnToken(token)).toReturn(expectedResponse);

        ResponseEntity responseEntity = userController.authenticateUser(token);

        Mockito.verify(userAuthService).authenticateUserBaseOnToken(token);
        assertEquals(expectedResponse.getStatusCode(), responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }

    @Test
    public void renewToken() {
        String token = "testToken";
        String renewedToken = "renewedToken";
        ResponseEntity expectedResponse = new ResponseEntity<>(renewedToken, HttpStatus.OK);
        Mockito.stub(tokenRenewService.renewTokenExpirationDate(token)).toReturn(expectedResponse);

        ResponseEntity responseEntity = userController.renewToken(token);

        Mockito.verify(tokenRenewService).renewTokenExpirationDate(token);
        assertEquals(expectedResponse.getStatusCode(), responseEntity.getStatusCode());
        assertEquals(expectedResponse.getBody(), responseEntity.getBody());
    }
}
