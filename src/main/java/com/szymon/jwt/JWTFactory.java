package com.szymon.jwt;

import com.szymon.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JWTFactory {
    public String createJwt(User user, String secret, Date expirationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Date issueAt = calendar.getTime();

        return Jwts.builder()
                .setExpiration(expirationDate)
                .setIssuedAt(issueAt)
                .claim("userId", user.getId())
                .claim("login", user.getLogin())
                .claim("role", user.getRole())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

}
