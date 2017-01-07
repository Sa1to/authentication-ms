package com.szymon.service;

import com.szymon.Texts.Responses;
import com.szymon.dao.UserDao;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${domain}")
    private String issuer;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JWTFactory jwtFactory;

    @Autowired
    private TokenService tokenService;

    @Override
    public ResponseEntity authenticateUserBaseOnCredentials(String login, String password) {
        User user = userDao.findByLogin(login);
        if (user == null || !(BCrypt.checkpw(password, user.getPassword())))
            return new ResponseEntity<>(Responses.WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
        else if (!user.isActive())
            return new ResponseEntity<>(Responses.INACTIVE_USER, HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(createAndSaveToken(user), HttpStatus.OK);
    }

    @Override
    public ResponseEntity authenticateUserBaseOnToken(String token) {
        try {
            tokenService.validateToken(token, secret);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(Responses.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            return new ResponseEntity<>(Responses.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            return new ResponseEntity<>(Responses.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public String createAndSaveToken(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);// expires claim. In this case the token expires in 5 minutes
        Date expiration = calendar.getTime();

        String jwt = jwtFactory.createJwt(user, secret, expiration);

        return jwt;
    }
}
