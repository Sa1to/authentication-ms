package com.szymon;

import com.szymon.Texts.RoleEnum;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import com.szymon.jwt.util.UserIdFromClaimsExtractor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class UserIdFromClaimsExtractorTests {

    @Mock
    private JWTFactory mockedJwtFactory;

    @InjectMocks
    private UserIdFromClaimsExtractor extractor = new UserIdFromClaimsExtractor();

    private JWTFactory jwtFactory = new JWTFactory();
    private String password = "pAssw0rd";
    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER, true);
    }

    @Test
    public void extractUserIdFromClaims() {
        ObjectId expectedId = new ObjectId(new Date(System.currentTimeMillis()), 123);
        user.setId(expectedId);
        String testSecret = "SECRET";
        String jwt = jwtFactory.createJwt(user, testSecret);

        Jws<Claims> claims = new DefaultJws<>(null, new DefaultClaims(new HashMap<String, Object>() {{
            put("userId", new LinkedHashMap<String, Object>(){{
                put("timestamp", expectedId.getTimestamp());
                put("machineIdentifier", expectedId.getMachineIdentifier());
                put("processIdentifier", (int) expectedId.getProcessIdentifier());
                put("counter", expectedId.getCounter());
            }});
        }}), testSecret);

        Mockito.stub(mockedJwtFactory.getClaimsFromToken(jwt, testSecret)).toReturn(claims);

        ObjectId actualId = extractor.extractUserIdFromToken(jwt, testSecret);

        assertEquals(expectedId, actualId);
    }
}