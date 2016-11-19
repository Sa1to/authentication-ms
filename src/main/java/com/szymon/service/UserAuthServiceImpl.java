package com.szymon.service;

import com.auth0.jwt.JWTSigner;
import com.szymon.Texts.Responses;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDao;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${domain}")
    private String issuer;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private JWTFactory jwtFactory;

    @Override
    public ResponseEntity authenticateUser(String login, String password) {
        User user = userDao.findByLogin(login);
        if (user == null || !(BCrypt.checkpw(password, user.getPassword())))
            return new ResponseEntity(Responses.WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
        else if (!user.isActive())
            return new ResponseEntity(Responses.INACTIVE_USER, HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity(createToken(userDao.findByLogin(login)), HttpStatus.OK);
    }

    @Override
    public String createToken(User user) {
        final long iat = System.currentTimeMillis() / 1000L; // issued at claim
        final long exp = iat + 60L; // expires claim. In this case the token expires in 60 seconds

        JWTSigner signer = jwtFactory.createJWTSigner(secret);

        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("iss", issuer);
        claims.put("exp", exp);
        claims.put("iat", iat);
        claims.put("login", user.getLogin());
        claims.put("role", user.getRole());

        String jwt = signer.sign(claims);
        tokenDao.save(jwtFactory.createToken(user.getId(), jwt));

        return jwt;
    }
}
