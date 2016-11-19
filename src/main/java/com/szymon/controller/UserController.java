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

    @RequestMapping(Urls.LOGIN)
    public ResponseEntity loginUser(@RequestParam("login") String login,
                                    @RequestParam("password") String password) {
        return userAuthService.authenticateUser(login, password);
    }
}
