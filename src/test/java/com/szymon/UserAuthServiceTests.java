package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.UserDao;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import com.szymon.jwt.util.UserIdFromClaimsExtractor;
import com.szymon.service.TokenService;
import com.szymon.service.UserAuthService;
import com.szymon.service.UserAuthServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class UserAuthServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private JWTFactory jwtFactory;

    @Mock
    private UserIdFromClaimsExtractor extractor;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserAuthService userAuthService = new UserAuthServiceImpl();

    @InjectMocks
    private UserAuthService userAuthServiceWithMockedMethod = new UserAuthServiceImpl() {
        @Override
        public String createAndSaveToken(User user) {
            return "testToken";
        }
    };

    private User user;
    private String password = RandomStringUtils.random(7, true, true);
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

        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(userWithHashedPassword);

        ResponseEntity isAuthenticated = userAuthServiceWithMockedMethod.authenticateUserBaseOnCredentials(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.OK, isAuthenticated.getStatusCode());
        assertEquals(token, isAuthenticated.getBody());
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void authenticateUserWithWrongCredentials() {
        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(null);

        ResponseEntity isAuthenticated = userAuthService.authenticateUserBaseOnCredentials(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.BAD_REQUEST, isAuthenticated.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, isAuthenticated.getBody());
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void authenticateInactiveUser() {
        User userWithHashedPassword = new User("jankowalski", "Jan", "Kowalski", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()), RoleEnum.USER, false);

        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(userWithHashedPassword);

        ResponseEntity isAuthenticated = userAuthService.authenticateUserBaseOnCredentials(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.BAD_REQUEST, isAuthenticated.getStatusCode());
        assertEquals(Responses.INACTIVE_USER, isAuthenticated.getBody());
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void createTokenTest() throws Exception {
        String stringToken = "testToken";

        Mockito.stub(jwtFactory.createJwt(eq(user), eq(testSecret), any())).toReturn(stringToken);

        userAuthService.createAndSaveToken(user);

        Mockito.verify(jwtFactory).createJwt(eq(user), eq(testSecret), any());
    }

    @Test
    public void authenticateUserWithCorrectToken() {
        String stringToken = "testToken";

        ResponseEntity responseEntity = userAuthService.authenticateUserBaseOnToken(stringToken);

        Mockito.verify(tokenService).validateToken(stringToken, testSecret);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void authenticateUserWithInvalidToken() {
        String stringToken = "invalidToken";
        Mockito.doThrow(new MalformedJwtException("message")).when(tokenService).validateToken(stringToken, testSecret);

        ResponseEntity responseEntity = userAuthService.authenticateUserBaseOnToken(stringToken);

        Mockito.verify(tokenService).validateToken(stringToken, testSecret);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(Responses.INVALID_TOKEN, responseEntity.getBody());
    }

    @Test
    public void authenticateUserWithExpiredToken() {
        String stringToken = "expiredToken";
        Mockito.doThrow(new ExpiredJwtException(new DefaultHeader(), new DefaultClaims(), "message")).when(tokenService).validateToken(stringToken, testSecret);

        ResponseEntity responseEntity = userAuthService.authenticateUserBaseOnToken(stringToken);

        Mockito.verify(tokenService).validateToken(stringToken, testSecret);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(Responses.TOKEN_EXPIRED, responseEntity.getBody());
    }
}
