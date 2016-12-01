package com.szymon;

import com.szymon.jwt.util.UserIdFromClaimsExtractor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class UserIdFromClaimsExtractorTests {

    private UserIdFromClaimsExtractor extractor = new UserIdFromClaimsExtractor();

    @Before
    public void setUp() {

    }

    @Test
    public void extractUserIdFromClaims() {
        ObjectId expectedId = new ObjectId(new Date(System.currentTimeMillis()), 123);

        Jws<Claims> claims = new DefaultJws<>(null, new DefaultClaims(new HashMap<String, Object>() {{
            put("userId", new LinkedHashMap<String, Object>(){{
                put("timestamp", expectedId.getTimestamp());
                put("machineIdentifier", expectedId.getMachineIdentifier());
                put("processIdentifier", (int) expectedId.getProcessIdentifier());
                put("counter", expectedId.getCounter());
            }});
        }}), "SECRET");

        ObjectId actualId = extractor.extractUserIdFromClaims(claims);

        assertEquals(expectedId, actualId);
    }
}