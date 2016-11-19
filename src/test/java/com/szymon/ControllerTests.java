package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.controller.UserController;
import com.szymon.dao.UserDao;
import com.szymon.entity.User;
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
    private UserDao userDao;

    @InjectMocks
    private UserController userController = new UserController();

    private String testCorrectLogin = "testLogin";
    private String testCorrectPassword = "testPassword";
    private User testUser = new User();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendCorrectCredentials() {
        String testToken = "testToken";

        Mockito.stub(userAuthService.authenticateUser(testCorrectLogin, testCorrectPassword)).toReturn(true);
        Mockito.stub(userDao.findByLogin(testCorrectLogin)).toReturn(testUser);
        Mockito.stub(userAuthService.createToken(testUser)).toReturn(testToken);

        ResponseEntity responseEntity = userController.loginUser(testCorrectLogin, testCorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testCorrectLogin, testCorrectPassword);
        Mockito.verify(userDao).findByLogin(testCorrectLogin);
        Mockito.verify(userAuthService).createToken(testUser);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(testToken, responseEntity.getBody().toString());
    }

    @Test
    public void sendWrongCredentials() {
        String testIncorrectPassword = "wrong";
        Mockito.stub(userAuthService.authenticateUser(testCorrectLogin, testIncorrectPassword)).toReturn(false);

        ResponseEntity responseEntity = userController.loginUser(testCorrectLogin, testIncorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testCorrectLogin, testIncorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody().toString());

        String testIncorrectLogin = "wrong";
        Mockito.stub(userAuthService.authenticateUser(testIncorrectLogin, testCorrectPassword)).toReturn(false);

        responseEntity = userController.loginUser(testIncorrectLogin, testCorrectPassword);

        Mockito.verify(userAuthService).authenticateUser(testIncorrectLogin, testCorrectPassword);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody().toString());
    }

}
