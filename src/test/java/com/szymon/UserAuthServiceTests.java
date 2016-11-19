package com.szymon;

import com.auth0.jwt.JWTSigner;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDao;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class UserAuthServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private TokenDao tokenDao;

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
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER, true);
        ReflectionTestUtils.setField(userAuthService, "secret", testSecret);
    }

    @Test
    public void authenticateUserTest() {
        User userWithHashedPassword = new User("jankowalski", "Jan", "Kowalski", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()), RoleEnum.USER, true);
        String token = "testToken";

        Mockito.stub(jwtFactory.createJWTSigner(testSecret)).toReturn(signer);
        Mockito.stub(signer.sign(any())).toReturn(token);
        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(userWithHashedPassword);

        ResponseEntity isAuthenticated = userAuthService.authenticateUser(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.OK, isAuthenticated.getStatusCode());
        assertEquals(token, isAuthenticated.getBody().toString());
        Mockito.verify(userDao, Mockito.times(2)).findByLogin(user.getLogin());
    }

    @Test
    public void createTokenTest() throws Exception {
        String mockToken = "mock token";

        Mockito.stub(jwtFactory.createJWTSigner(testSecret)).toReturn(signer);
        Mockito.stub(signer.sign(any())).toReturn(mockToken);

        Token testToken = new Token(user.getId(), mockToken);
        Mockito.stub(jwtFactory.createToken(user.getId(), mockToken)).toReturn(testToken);

        userAuthService.createToken(user);

        Mockito.verify(tokenDao).save(testToken);
    }
}
