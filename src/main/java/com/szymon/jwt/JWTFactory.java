package com.szymon.jwt;

import com.szymon.domain.Token;
import com.szymon.domain.User;
import io.jsonwebtoken.*;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JWTFactory {
    public String createJwt(User user, String secret) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Date issueAt = calendar.getTime();

        calendar.add(Calendar.MINUTE, 5);// expires claim. In this case the token expires in 5 minutes

        Date expiration = calendar.getTime();

        return Jwts.builder()
                .setExpiration(expiration)
                .setIssuedAt(issueAt)
                .claim("userId", user.getId())
                .claim("login", user.getLogin())
                .claim("role", user.getRole())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Token createToken(ObjectId userId, String jwt) {
        return new Token(userId, jwt);
    }

    public Jws<Claims> getClaimsFromToken(String token, String secret) throws SignatureException {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
    }
}
