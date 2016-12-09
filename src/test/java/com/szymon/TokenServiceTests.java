package com.szymon;

import com.szymon.domain.Token;
import com.szymon.jwt.JWTFactory;
import com.szymon.service.TokenService;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class TokenServiceTests {

    @Mock
    private JWTFactory jwtFactory;

    @InjectMocks
    private TokenService tokenService = new TokenService();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkIfTokenExpired() {
        String token = "token";
        String secret = "secret";

        Mockito.stub(jwtFactory.getClaimsFromToken(token, secret)).toReturn(new DefaultJws<>(null, new DefaultClaims(new HashMap<String, Object>() {{
            put("exp", System.currentTimeMillis() + 1000);
        }}), secret));

        boolean isValid = tokenService.validateTokenExpirationDate(token, secret);

        assertTrue(isValid);
    }
}
