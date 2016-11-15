package com.szymon.service;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.internal.com.fasterxml.jackson.core.JsonToken;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDao;
import com.szymon.entity.Token;
import com.szymon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public boolean authenticateUser(String login, String password) {
        User user = userDao.findByLogin(login);
        return user != null && (BCrypt.checkpw(password, user.getPassword()));
    }

    @Override
    public String createToken(User user) {
        final long iat = System.currentTimeMillis() / 1000L; // issued at claim
        final long exp = iat + 60L; // expires claim. In this case the token expires in 60 seconds

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("iss", issuer);
        claims.put("exp", exp);
        claims.put("iat", iat);
        claims.put("login", user.getLogin());
        claims.put("role", user.getRole());

        String jwt = signer.sign(claims);
        tokenDao.save(new Token(user.getId(),jwt));
        
        return jwt;
    }
}
