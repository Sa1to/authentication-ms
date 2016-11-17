package com.szymon.jwt;

import com.auth0.jwt.JWTSigner;
import com.szymon.entity.Token;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class JWTFactory {
    public JWTSigner createJWTSigner(String secret){
        return new JWTSigner(secret);
    }
    public Token createToken(ObjectId userId,String jwt){
        return new Token(userId,jwt);
    }
}
