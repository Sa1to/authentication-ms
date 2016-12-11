package com.szymon;


import com.szymon.Texts.Responses;
import com.szymon.controller.UserController;
import com.szymon.dao.ActivationCodeDao;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDaoImpl;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.Credentials;
import com.szymon.domain.Token;
import com.szymon.domain.User;
import com.szymon.Texts.RoleEnum;
import com.szymon.jwt.JWTFactory;
import com.szymon.service.UserAuthService;
import org.apache.catalina.connector.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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

    @Autowired
    private JWTFactory jwtFactory;

    @Value("${jwt.secret}")
    private String secret;

    private String password = "pAssw0rd";
    private User user;
    private Credentials credentials;

    @Before
    public void setup() {
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER, true);
        credentials = new Credentials(user.getLogin(), password);
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
        ResponseEntity response = userController.loginUser(credentials);
        String token = response.getBody().toString();
        assertEquals(token, tokenDao.findByUserId(user.getId()).getToken());
        assertNotEquals(Responses.WRONG_CREDENTIALS, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void loginAsUserWithWrongPassword() {
        userDao.save(user);
        credentials.setPassword("wrongPassword");
        ResponseEntity responseEntity = userController.loginUser(credentials);
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void loginAsUserWithWrongLogin() {
        userDao.save(user);
        credentials.setLogin("wrongLogin");
        ResponseEntity responseEntity = userController.loginUser(credentials);
        assertEquals(Responses.WRONG_CREDENTIALS, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void loginAsInactiveUser() {
        User inactiveUser = user;
        inactiveUser.setActive(false);
        userDao.save(inactiveUser);

        ResponseEntity responseEntity = userController.loginUser(credentials);

        assertEquals(Responses.INACTIVE_USER, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void registerUser() {
        User inactiveUser = user;
        inactiveUser.setActive(false);

        ResponseEntity responseEntity = userController.registerUser(inactiveUser);

        ActivationCode activationCode = activationCodeDao.findByUserId(inactiveUser.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Responses.ACTIVATION_CODE_SENT, responseEntity.getBody());
        assertNotNull(activationCode);
        assertEquals(activationCode.getUserId(), inactiveUser.getId());
    }

    @Test
    public void registerUserWithMissingFields() {
        User nullUser = new User();

        ResponseEntity responseEntity = userController.registerUser(nullUser);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(Responses.USER_FIELDS_LACKING, responseEntity.getBody());
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

        assertEquals(Responses.USER_ACTIVATED, responseEntity.getBody());
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

        assertEquals(Responses.INCORRECT_ACTIVATION_CODE, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(activeUser.isActive());
    }

    @Test
    public void logoutAuthenticatedUser() {
        userDao.save(user);

        String tokenString = userController.loginUser(credentials).getBody().toString();

        ResponseEntity responseEntity = userController.logoutUser(tokenString);

        assertNull(tokenDao.findByUserId(user.getId()));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void loginAsUserWhenThereIsTokenCreatedForThisUser() {
        userDao.save(user);
        String tokenString = userAuthService.createAndSaveToken(user);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity responseEntity = userController.loginUser(credentials);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(tokenDao.findByStringTokenValue(tokenString));
        assertEquals(tokenDao.findByUserId(user.getId()).getToken(), responseEntity.getBody());
    }

    @Test
    public void authenticateUserWithCorrectToken() {
        String tokenString = userAuthService.createAndSaveToken(user);

        ResponseEntity responseEntity = userController.authenticateUser(tokenString);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void authenticateUserWithIncorrectToken() {
        String invalidToken = "token";

        ResponseEntity responseEntity = userController.authenticateUser(invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_TOKEN, responseEntity.getBody());
    }

    @Test
    public void authenticateUserWithExpiredToken() {
        String tokenString = jwtFactory.createJwt(user, secret, new Date(System.currentTimeMillis() - 60 * 1000));
        Token token = new Token(user.getId(), tokenString);
        tokenDao.save(token);

        ResponseEntity responseEntity = userController.authenticateUser(tokenString);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(Responses.TOKEN_EXPIRED, responseEntity.getBody());
        assertNull(tokenDao.findByStringTokenValue(token.getToken()));
    }

    @Test
    public void asUserRenewToken() {
        String tokenString = jwtFactory.createJwt(user, secret, new Date(System.currentTimeMillis() + 60 * 1000));
        Token token = new Token(user.getId(), tokenString);
        tokenDao.save(token);

        ResponseEntity responseRenew = userController.renewToken(tokenString);
        ResponseEntity responseAuth = userController.authenticateUser(responseRenew.getBody().toString());

        assertEquals(HttpStatus.OK, responseRenew.getStatusCode());
        assertNotEquals(tokenString, responseRenew.getBody());

        assertEquals(HttpStatus.OK, responseAuth.getStatusCode());
    }

    @After
    public void shutdown() {
        datastore.getDB().dropDatabase();
    }
}
