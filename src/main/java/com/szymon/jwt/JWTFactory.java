package com.szymon.jwt;

import com.szymon.domain.Token;
import io.jsonwebtoken.*;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JWTFactory {
    public String createJwt(Map<String, Object> claims, String secret) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Token createToken(ObjectId userId, String jwt) {
        return new Token(userId, jwt);
    }

    public Jws<Claims> createClaims(String secret, String token) throws SignatureException {
return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token);
    }
}
