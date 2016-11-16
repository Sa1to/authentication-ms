package com.szymon;

import com.szymon.Texts.RoleEnum;
import com.szymon.dao.TokenDaoImpl;
import com.szymon.dao.UserDaoImpl;
import com.szymon.entity.User;
import com.szymon.service.UserAuthService;
import com.szymon.service.UserAuthServiceImpl;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserAuthServiceTests {

    @Mock
    private UserDaoImpl userDao;

    @Mock
    private TokenDaoImpl tokenDao;

    @InjectMocks
    private UserAuthService userAuthServiceTests = new UserAuthServiceImpl();

    private User user;
    private String password = RandomStringUtils.random(7);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER);
    }

    @Test
    public void authenticateUserTest(){
        User userWithHashedPassword = new User("jankowalski", "Jan", "Kowalski", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()), RoleEnum.USER);
        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(userWithHashedPassword);
        boolean isAuthenticated = userAuthServiceTests.authenticateUser(user.getLogin(),user.getPassword());
        assert(isAuthenticated);
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }
}
