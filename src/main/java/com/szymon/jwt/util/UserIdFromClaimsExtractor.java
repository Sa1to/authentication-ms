package com.szymon.jwt.util;

import com.szymon.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class UserIdFromClaimsExtractor {

    @Autowired
    private TokenService jwtFactory;

    public ObjectId extractUserIdFromToken(String token, String secret) throws NullPointerException, SignatureException, MalformedJwtException {
        Jws<Claims> claims = jwtFactory.getClaimsFromToken(token, secret);
        LinkedHashMap<String, Object> userId = (LinkedHashMap<String, Object>) claims.getBody().get("userId");
        return new ObjectId((int) userId.get("timestamp"),
                (int) userId.get("machineIdentifier"),
                (short) (int) userId.get("processIdentifier"),
                (int) userId.get("counter"));
    }
}
