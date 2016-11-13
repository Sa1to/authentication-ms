package com.szymon;


import com.szymon.dao.UserDao;
import com.szymon.entity.User;
import com.szymon.Texts.RoleEnum;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationMsApplicationTests {

    @Autowired
    private UserDao userDao;

    private String password  = RandomStringUtils.random(7);

    @Test
    public void contextLoads() {
    }

    @Test
    public void writeUserToDatabase() {
        User user = new User("Jan", "Kowalski", password , RoleEnum.USER);
        userDao.saveWithHashedPassword(user);
        ArrayList<User> retrievedUser = (ArrayList<User>) userDao.findByNameAndSurname(user.getName(), user.getSurname());
        assert(retrievedUser.size()==1);
        assert(BCrypt.checkpw(password,retrievedUser.get(0).getPassword()));
        userDao.delete(user);
    }
}
