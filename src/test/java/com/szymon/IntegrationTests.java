package com.szymon;


import com.szymon.Texts.Responses;
import com.szymon.controller.UserController;
import com.szymon.dao.UserDao;
import com.szymon.dao.UserDaoImpl;
import com.szymon.entity.User;
import com.szymon.Texts.RoleEnum;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationMsApplicationTests {

    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private UserController userController;

    @Autowired
    private Datastore datastore;

    private String password = RandomStringUtils.random(7);
    private User user;

    @Before
    public void setup() {
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER);
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void writeUserToDatabaseTest() {
        userDao.saveWithHashedPassword(user);
        User retrievedUser = userDao.findByLogin(user.getLogin());
        assert (!user.getPassword().equals(password));
        assert (BCrypt.checkpw(password, retrievedUser.getPassword()));
    }

    @Test
    public void loginAsUserTest() {
        userDao.saveWithHashedPassword(user);
        ResponseEntity response = userController.loginController(user.getLogin(), password);
        assert(!response.getBody().equals(Responses.WRONG_CREDENTIALS));
    }



    @After
    public void shutdown() {
        datastore.getDB().dropDatabase();
    }
}
