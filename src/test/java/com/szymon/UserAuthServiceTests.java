package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDao;
import com.szymon.domain.Token;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import com.szymon.jwt.util.UserIdFromClaimsExtractor;
import com.szymon.service.TokenService;
import com.szymon.service.UserAuthService;
import com.szymon.service.UserAuthServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJws;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.types.ObjectId;
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

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class UserAuthServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private TokenDao tokenDao;

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
        Token testToken = new Token(user.getId(), stringToken);
        Date date = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        Mockito.stub(jwtFactory.createJwt(eq(user), eq(testSecret), any())).toReturn(stringToken);
        Mockito.stub(jwtFactory.createToken(user.getId(), stringToken)).toReturn(testToken);
        Mockito.stub(tokenDao.findByUserId(user.getId())).toReturn(null);

        userAuthService.createAndSaveToken(user);

        Mockito.verify(tokenDao).findByUserId(user.getId());
        Mockito.verify(tokenDao).save(testToken);
        Mockito.verify(jwtFactory).createJwt(eq(user), eq(testSecret), any());
    }

    @Test
    public void createTokenWhenThereIsAlreadyOneCreated() {
        String stringToken = "testToken";
        Token oldToken = new Token();
        Date date = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        Mockito.stub(jwtFactory.createJwt(user, testSecret, date)).toReturn(stringToken);

        Token testToken = new Token(user.getId(), stringToken);
        Mockito.stub(jwtFactory.createToken(user.getId(), stringToken)).toReturn(testToken);
        Mockito.stub(tokenDao.findByUserId(user.getId())).toReturn(oldToken);

        userAuthService.createAndSaveToken(user);

        Mockito.verify(tokenDao).findByUserId(user.getId());
        Mockito.verify(tokenDao).delete(oldToken);
        Mockito.verify(tokenDao).save(testToken);
        Mockito.verify(jwtFactory).createJwt(user, testSecret, date);
    }

    @Test
    public void validateAndRemoveTokenTest() {
        String testToken = "testToken";
        User user = new User("jankowalski", "Jan", "Kowalski", "Passw0rd", RoleEnum.USER, true);
        Token token = new Token(user.getId(), testToken);
        Jws<Claims> claims = new DefaultJws<>(null, new DefaultClaims(new HashMap<String, Object>() {{
            put("userId", new ObjectId());
        }}), testSecret);

        Mockito.stub(extractor.extractUserIdFromToken(testToken, testSecret)).toReturn(user.getId());
        Mockito.stub(tokenDao.findByUserId(user.getId())).toReturn(token);

        ResponseEntity responseEntity = userAuthService.validateAndRemoveToken(testToken);

        Mockito.verify(extractor).extractUserIdFromToken(testToken, testSecret);
        Mockito.verify(tokenDao).findByUserId(user.getId());
        Mockito.verify(tokenDao).delete(token);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void authenticateUserWithCorrectToken() {
        String stringToken = "testToken";
        Token token = new Token(null, stringToken);

        Mockito.stub(tokenDao.findByStringTokenValue(stringToken)).toReturn(token);

        ResponseEntity responseEntity = userAuthService.authenticateUserBaseOnToken(stringToken);

        Mockito.verify(tokenDao).findByStringTokenValue(stringToken);
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
        Token token = new Token(null, stringToken);
        Mockito.stub(tokenDao.findByStringTokenValue(stringToken)).toReturn(token);
        Mockito.doThrow(new ExpiredJwtException(new DefaultHeader(), new DefaultClaims(), "message")).when(tokenService).validateToken(stringToken, testSecret);

        ResponseEntity responseEntity = userAuthService.authenticateUserBaseOnToken(stringToken);

        Mockito.verify(tokenService).validateToken(stringToken, testSecret);
        Mockito.verify(tokenDao).findByStringTokenValue(stringToken);
        Mockito.verify(tokenDao).delete(token);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(Responses.TOKEN_EXPIRED, responseEntity.getBody());
    }
}
