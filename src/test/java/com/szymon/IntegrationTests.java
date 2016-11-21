package com.szymon;


import com.szymon.Texts.Responses;
import com.szymon.controller.UserController;
import com.szymon.dao.RegistrationCodeDao;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDaoImpl;
import com.szymon.domain.RegistrationCode;
import com.szymon.domain.User;
import com.szymon.Texts.RoleEnum;
import org.apache.commons.lang.RandomStringUtils;
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
    private RegistrationCodeDao registrationCodeDao;

    private String password = RandomStringUtils.random(10, true, true);
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

        RegistrationCode registrationCode = registrationCodeDao.findByUserId(user.getId());

        assertNotNull(registrationCode);
        assertEquals(registrationCode.getUserId(), user.getId());
    }

    @After
    public void shutdown() {
        datastore.getDB().dropDatabase();
    }
}
