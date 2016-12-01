package com.szymon.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class UserIdFromClaimsExtractor {

    public ObjectId extractUserIdFromClaims(Jws<Claims> claims){
        LinkedHashMap<String, Object> userId = (LinkedHashMap<String, Object>) claims.getBody().get("userId");
        return new ObjectId((int) userId.get("timestamp"),
                (int) userId.get("machineIdentifier"),
                (short) (int) userId.get("processIdentifier"),
                (int) userId.get("counter"));
    }
}
