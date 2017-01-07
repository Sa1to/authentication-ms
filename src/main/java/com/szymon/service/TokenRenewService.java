package com.szymon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class TokenRenewService {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity renewTokenExpirationDate(String token) {
        ResponseEntity responseEntity = userAuthService.authenticateUserBaseOnToken(token);

        if (responseEntity.getStatusCode() != HttpStatus.OK)
            return responseEntity;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);// expires claim. In this case the token expires in 5 minutes
        Date expiration = calendar.getTime();

        String renewedToken = tokenService.updateExpiration(token, secret, expiration);

        return new ResponseEntity<>(renewedToken, HttpStatus.OK);
    }
}
