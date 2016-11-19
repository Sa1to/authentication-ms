package com.szymon.controller;

import com.szymon.Texts.Responses;
import com.szymon.Texts.Urls;
import com.szymon.dao.UserDao;
import com.szymon.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Urls.AUTH)
public class UserController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserDao userDao;

    @RequestMapping(Urls.LOGIN)
    public ResponseEntity loginUser(@RequestParam("login") String login,
                                    @RequestParam("password") String password) {
        if (userAuthService.authenticateUser(login, password))
            return new ResponseEntity(userAuthService.createToken(userDao.findByLogin(login)), HttpStatus.OK);
        else
            return new ResponseEntity(Responses.WRONG_CREDENTIALS, HttpStatus.BAD_REQUEST);
    }
}
