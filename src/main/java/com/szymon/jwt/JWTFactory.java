package com.szymon.jwt;

import com.auth0.jwt.JWTSigner;
import org.springframework.stereotype.Component;

@Component
public class JWTSignerFactory {
    public JWTSigner createJWTSigner(String secret){
        return new JWTSigner(secret);
    }
}
