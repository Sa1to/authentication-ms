package com.szymon;


import com.szymon.Texts.Responses;
import com.szymon.controller.UserController;
import com.szymon.dao.ActivationCodeDao;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDaoImpl;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.Token;
import com.szymon.domain.User;
import com.szymon.Texts.RoleEnum;
import com.szymon.service.UserAuthService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationTests {

    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private UserController userController;

    @Autowired
    private Datastore datastore;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private ActivationCodeDao activationCodeDao;

    @Autowired
    private UserAuthService userAuthService;

    private String password = "pAssw0rd";
    private User user;

    @Before
    public void setup() {
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER, false);
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void writeUserToDatabaseTest() {
        userDao.save(user);
        User retrievedUser = userDao.findByLogin(user.getLogin());
        assertNotEquals(user.getPassword(), password);
        assertTrue(BCrypt.checkpw(password, retrievedUser.getPassword()));
    }

    @Test
    public void loginAsUserTest() {
        user.setActive(true);
        userDao.save(user);
        ResponseEntity response = userController.loginUser(user.getLogin(), password);
        String token = response.getBody().toString();
        assertEquals(token, tokenDao.findByUserId(user.getId()).getToken());
        assertNotEquals(Responses.WRONG_CREDENTIALS, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void loginAsUserWithWrongPassword() {
        userDao.save(user);
        ResponseEntity responseEntity = userController.loginUser(user.getLogin(), "WRONG PASSWORD");
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void loginAsUserWithWrongLogin() {
        userDao.save(user);
        ResponseEntity responseEntity = userController.loginUser("WRONG LOGIN", password);
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void loginAsInactiveUser() {
        User inactiveUser = user;
        inactiveUser.setActive(false);
        userDao.save(inactiveUser);

        ResponseEntity responseEntity = userController.loginUser(inactiveUser.getLogin(), password);

        assertEquals(Responses.INACTIVE_USER, responseEntity.getBody().toString());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void registerUser() {
        userController.registerUser(user);

        ActivationCode activationCode = activationCodeDao.findByUserId(user.getId());

        assertNotNull(activationCode);
        assertEquals(activationCode.getUserId(), user.getId());
    }

    @Test
    public void activateUser() {
        String testCode = "testCode";

        User inactiveUser = user;
        inactiveUser.setActive(false);
        userDao.save(inactiveUser);

        ActivationCode activationCode = new ActivationCode(inactiveUser.getId(), testCode);
        activationCodeDao.save(activationCode);

        ResponseEntity responseEntity = userController.activateUser(testCode);

        User activeUser = userDao.findById(inactiveUser.getId());

        assertEquals(Responses.USER_ACTIVATED, responseEntity.getBody().toString());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(activeUser.isActive());
        assertNull(activationCodeDao.findByCode(testCode));
    }

    @Test
    public void tryToActivateUserWithWrongCode() {
        String testCode = "testCode";

        User inactiveUser = user;
        inactiveUser.setActive(false);
        userDao.save(inactiveUser);

        ActivationCode activationCode = new ActivationCode(inactiveUser.getId(), testCode);
        activationCodeDao.save(activationCode);

        ResponseEntity responseEntity = userController.activateUser("wrongCode");

        User activeUser = userDao.findById(inactiveUser.getId());

        assertEquals(Responses.INCORRECT_ACTIVATION_CODE, responseEntity.getBody().toString());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(activeUser.isActive());
    }

    @Test
    public void logoutAuthenticatedUser() {
        userDao.save(user);
        String tokenString = userAuthService.createToken(user);

        ResponseEntity responseEntity = userController.logoutUser(tokenString);

        assertNull(tokenDao.findByUserId(user.getId()));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @After
    public void shutdown() {
        datastore.getDB().dropDatabase();
    }
}
