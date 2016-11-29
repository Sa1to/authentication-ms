package com.szymon.jwt;

import com.auth0.jwt.JWTSigner;
import com.szymon.domain.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class JWTFactory {
    public JWTSigner createJWTSigner(String secret) {
        return new JWTSigner(secret);
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
