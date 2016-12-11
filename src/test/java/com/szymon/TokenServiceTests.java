package com.szymon;

import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import com.szymon.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TokenServiceTests {

    private JWTFactory jwtFactory = new JWTFactory();

    private TokenService tokenService = new TokenService();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkIfTokenExpired() {
        User user = new User();
        String secret = "secret";
        String jwt = jwtFactory.createJwt(user, secret, new Date(System.currentTimeMillis() + 1 * 60 * 1000));

        tokenService.validateToken(jwt, secret);
    }

    @Test
    public void renewToken(){
        User user = new User();
        String secret = "secret";
        String jwt = jwtFactory.createJwt(user, secret, new Date(System.currentTimeMillis() + 1 * 60 * 1000));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 5 * 60 * 1000);
        Date newDate = calendar.getTime();

        jwt = tokenService.updateExpiration(jwt, secret, newDate);

        Jws<Claims> claims = tokenService.getClaimsFromToken(jwt, secret);

        assertEquals(newDate.getDate(), claims.getBody().getExpiration().getDate());
    }
}