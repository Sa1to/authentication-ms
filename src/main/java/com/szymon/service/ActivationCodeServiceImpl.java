package com.szymon.service;

import com.szymon.Texts.Responses;
import com.szymon.dao.ActivationCodeDao;
import com.szymon.dao.UserDao;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    @Autowired
    private ActivationCodeDao activationCodeDao;

    @Autowired
    private UserDao userDao;

    @Override
    public ActivationCode createAndSave(User user) {
        ActivationCode activationCode = new ActivationCode(user.getId(), RandomStringUtils.random(20, true, true));
        activationCodeDao.save(activationCode);
        return activationCode;
    }

    @Override
    public ResponseEntity activateUser(String activationCode) {
        ActivationCode codeFromDb = activationCodeDao.findByCode(activationCode);
        if (codeFromDb == null) {
            return new ResponseEntity<>(Responses.INCORRECT_ACTIVATION_CODE, HttpStatus.BAD_REQUEST);
        }
        User user = userDao.findById(codeFromDb.getUserId());
        userDao.updateActivation(user, true);
        return new ResponseEntity<>(Responses.USER_ACTIVATED, HttpStatus.OK);
    }

}
