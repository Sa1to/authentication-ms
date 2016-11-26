package com.szymon.service;

import com.sendgrid.Response;
import com.szymon.Texts.Responses;
import com.szymon.dao.UserDao;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import com.szymon.service.mailing.MailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
public class RegistrationValidation implements RegistrationValidator {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ActivationCodeService activationCodeService;

    @Autowired
    private MailingService mailingService;

    @Override
    public ResponseEntity validateUserToRegistration(User user) {

        if (!validateLogin(user.getLogin()))
            return new ResponseEntity<>(Responses.INVALID_LOGIN, HttpStatus.BAD_REQUEST);

        if (!validatePassword(user.getPassword()))
            return new ResponseEntity<>(Responses.INVALID_PASSWORD, HttpStatus.BAD_REQUEST);

        if (user.isActive())
            return new ResponseEntity<>(Responses.ACTIVE_PARAM_NOT_ALLOWED, HttpStatus.BAD_REQUEST);

        User userFromDB = userDao.findByLogin(user.getLogin());
        if (userFromDB != null) {
            return new ResponseEntity<>(Responses.USER_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }

        userDao.save(user);
        ActivationCode activationCode = activationCodeService.createAndSave(user);
        try {
            mailingService.sendActivationCode(activationCode, user);
            return new ResponseEntity<>(Responses.ACTIVATION_CODE_SENT, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    private boolean validateLogin(String login) {
        return !(login.length() < 5 || login.length() > 20 || login.contains(" "));
    }

    private boolean validatePassword(String password) {
        if (password.length() < 8 || password.length() >= 40)
            return false;

        Pattern[] pattern = new Pattern[3];
        pattern[0] = Pattern.compile(".*[A-Z].*");
        pattern[1] = Pattern.compile(".*[a-z].*");
        pattern[2] = Pattern.compile(".*\\d.*");

        for (Pattern aPattern : pattern) {
            if (!aPattern.matcher(password).matches())
                return false;
        }

        return true;
    }
}
