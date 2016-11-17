package com.szymon;

import com.auth0.jwt.JWTSigner;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.TokenDaoImpl;
import com.szymon.dao.UserDaoImpl;
import com.szymon.entity.Token;
import com.szymon.entity.User;
import com.szymon.jwt.JWTFactory;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.any;

public class UserAuthServiceTests {

    @Mock
    private UserDaoImpl userDao;

    @Mock
    private TokenDaoImpl tokenDao;

    @Mock
    private JWTSigner signer;

    @Mock
    private JWTFactory jwtFactory;

    @InjectMocks
    private UserAuthService userAuthService = new UserAuthServiceImpl();

    private User user;
    private String password = RandomStringUtils.random(7);
    private String testSecret = "testSecret";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER);
    }

    @Test
    public void authenticateUserTest() {
        User userWithHashedPassword = new User("jankowalski", "Jan", "Kowalski", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()), RoleEnum.USER);
        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(userWithHashedPassword);
        boolean isAuthenticated = userAuthService.authenticateUser(user.getLogin(), user.getPassword());
        assert (isAuthenticated);
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void createTokenTest() throws Exception {
        ReflectionTestUtils.setField(userAuthService, "secret", testSecret);
        String mockToken = "mock token";
        Mockito.stub(jwtFactory.createJWTSigner(testSecret)).toReturn(signer);
        Mockito.stub(signer.sign(any())).toReturn(mockToken);
        Token testToken = new Token(user.getId(), mockToken);
        Mockito.stub(jwtFactory.createToken(user.getId(),mockToken)).toReturn(testToken);
        userAuthService.createToken(user);
        Mockito.verify(tokenDao).save(testToken);
    }
}
