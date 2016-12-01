package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDao;
import com.szymon.domain.Token;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import com.szymon.jwt.util.UserIdFromClaimsExtractor;
import com.szymon.service.UserAuthService;
import com.szymon.service.UserAuthServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
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

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
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

    @InjectMocks
    private UserAuthService userAuthService = new UserAuthServiceImpl();

    @InjectMocks
    private UserAuthService userAuthServiceWithMockedMethod = new UserAuthServiceImpl(){
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

        ResponseEntity isAuthenticated = userAuthServiceWithMockedMethod.authenticateUser(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.OK, isAuthenticated.getStatusCode());
        assertEquals(token, isAuthenticated.getBody().toString());
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void authenticateUserWithWrongCredentials() {
        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(null);

        ResponseEntity isAuthenticated = userAuthService.authenticateUser(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.BAD_REQUEST, isAuthenticated.getStatusCode());
        assertEquals(Responses.WRONG_CREDENTIALS, isAuthenticated.getBody().toString());
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void authenticateInactiveUser() {
        User userWithHashedPassword = new User("jankowalski", "Jan", "Kowalski", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()), RoleEnum.USER, false);

        Mockito.stub(userDao.findByLogin(user.getLogin())).toReturn(userWithHashedPassword);

        ResponseEntity isAuthenticated = userAuthService.authenticateUser(user.getLogin(), user.getPassword());

        assertEquals(HttpStatus.BAD_REQUEST, isAuthenticated.getStatusCode());
        assertEquals(Responses.INACTIVE_USER, isAuthenticated.getBody().toString());
        Mockito.verify(userDao).findByLogin(user.getLogin());
    }

    @Test
    public void createTokenTest() throws Exception {
        String stringToken = "testToken";

        Mockito.stub(jwtFactory.createJwt(any(),eq(testSecret))).toReturn(stringToken);

        Token testToken = new Token(user.getId(), stringToken);
        Mockito.stub(jwtFactory.createToken(user.getId(), stringToken)).toReturn(testToken);
        Mockito.stub(tokenDao.findByUserId(user.getId())).toReturn(null);

        userAuthService.createAndSaveToken(user);

        Mockito.verify(tokenDao).findByUserId(user.getId());
        Mockito.verify(tokenDao).save(testToken);
        Mockito.verify(jwtFactory).createJwt(any(),eq(testSecret));
    }

    @Test
    public void createTokenWhenThereIsAlreadyOneCreated(){
        String stringToken = "testToken";
        Token oldToken = new Token();

        Mockito.stub(jwtFactory.createJwt(any(),eq(testSecret))).toReturn(stringToken);

        Token testToken = new Token(user.getId(), stringToken);
        Mockito.stub(jwtFactory.createToken(user.getId(), stringToken)).toReturn(testToken);
        Mockito.stub(tokenDao.findByUserId(user.getId())).toReturn(oldToken);

        userAuthService.createAndSaveToken(user);

        Mockito.verify(tokenDao).findByUserId(user.getId());
        Mockito.verify(tokenDao).delete(oldToken);
        Mockito.verify(tokenDao).save(testToken);
        Mockito.verify(jwtFactory).createJwt(any(),eq(testSecret));
    }

    @Test
    public void validateAndRemoveTokenTest() {
        String testToken = "testToken";
        User user = new User("jankowalski", "Jan", "Kowalski", "Passw0rd", RoleEnum.USER, true);
        Token token = new Token(user.getId(), testToken);
        Jws<Claims> claims = new DefaultJws<>(null, new DefaultClaims(new HashMap<String, Object>()
        {{put("userId", new LinkedHashMap<String,Object>());}}),testSecret);

        Mockito.stub(jwtFactory.createClaims(testSecret, testToken)).toReturn(claims);
        Mockito.stub(extractor.extractUserIdFromClaims(claims)).toReturn(user.getId());
        Mockito.stub(userDao.findById(user.getId())).toReturn(user);
        Mockito.stub(tokenDao.findByUserId(user.getId())).toReturn(token);

        ResponseEntity responseEntity = userAuthService.validateAndRemoveToken(testToken);

        Mockito.verify(extractor).extractUserIdFromClaims(claims);
        Mockito.verify(jwtFactory).createClaims(testSecret, testToken);
        Mockito.verify(userDao).findById(any());
        Mockito.verify(tokenDao).findByUserId(user.getId());
        Mockito.verify(tokenDao).delete(token);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
