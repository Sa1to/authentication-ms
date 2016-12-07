package com.szymon.service;

import com.szymon.Texts.Responses;
import com.szymon.dao.TokenDao;
import com.szymon.dao.UserDao;
import com.szymon.domain.Token;
import com.szymon.domain.User;
import com.szymon.jwt.JWTFactory;
import com.szymon.jwt.util.UserIdFromClaimsExtractor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

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

    @Autowired
    private UserIdFromClaimsExtractor userIdFromClaimsExtractor;

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
        if (tokenDao.findByStringTokenValue(token) != null)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(Responses.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public String createAndSaveToken(User user) {
        String jwt = jwtFactory.createJwt(user, secret);

        Token oldToken = tokenDao.findByUserId(user.getId());
        if (oldToken != null)
            tokenDao.delete(oldToken);

        tokenDao.save(jwtFactory.createToken(user.getId(), jwt));

        return jwt;
    }

    @Override
    public ResponseEntity validateAndRemoveToken(String token) {
        ObjectId userId;
        try {
            userId = userIdFromClaimsExtractor.extractUserIdFromToken(token, secret);
        } catch (SignatureException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        catch (NullPointerException e) {
            return new ResponseEntity<>(Responses.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        Token jwt = tokenDao.findByUserId(userId);

        if (jwt.getToken().equals(token)) {
            tokenDao.delete(jwt);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(Responses.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

}
