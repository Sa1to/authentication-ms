package com.szymon.controller;

import com.szymon.Texts.Urls;
import com.szymon.domain.User;
import com.szymon.service.RegistrationValidator;
import com.szymon.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Urls.AUTH)
public class UserController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private RegistrationValidator registrationValidator;

    @RequestMapping(value = Urls.LOGIN, method = RequestMethod.GET)
    public ResponseEntity loginUser(@RequestParam("login") String login,
                                    @RequestParam("password") String password) {
        return userAuthService.authenticateUser(login, password);
    }

    @RequestMapping(Urls.REGISTER)
    public ResponseEntity registerUser(@RequestBody User user){
        return registrationValidator.validateUserToRegistration(user);
    }
}
