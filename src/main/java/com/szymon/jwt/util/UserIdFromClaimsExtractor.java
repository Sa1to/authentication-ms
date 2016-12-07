package com.szymon.jwt.util;

import com.szymon.jwt.JWTFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class UserIdFromClaimsExtractor {

    @Autowired
    private JWTFactory jwtFactory;

    public ObjectId extractUserIdFromToken(String token, String secret) throws NullPointerException, SignatureException {
        Jws<Claims> claims = jwtFactory.getClaimsFromToken(token, secret);
        LinkedHashMap<String, Object> userId = (LinkedHashMap<String, Object>) claims.getBody().get("userId");
        return new ObjectId((int) userId.get("timestamp"),
                (int) userId.get("machineIdentifier"),
                (short) (int) userId.get("processIdentifier"),
                (int) userId.get("counter"));
    }
}
