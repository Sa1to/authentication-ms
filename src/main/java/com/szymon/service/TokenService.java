package com.szymon.service;

import com.szymon.domain.Token;
import com.szymon.jwt.JWTFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class TokenService {

    @Autowired
    private JWTFactory jwtFactory;

    public boolean validateTokenExpirationDate(String token, String secret) {
        Jws<Claims> claims = jwtFactory.getClaimsFromToken(token, secret);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Date currentDate = calendar.getTime();

        Date tokenExpirationDate = claims.getBody().getExpiration();

        return currentDate.before(tokenExpirationDate);
    }

}
