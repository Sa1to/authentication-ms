package com.szymon.controller;

import com.szymon.Texts.Uri;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.RegistrationValidator;
import com.szymon.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Uri.AUTH)
public class UserController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private RegistrationValidator registrationValidator;

    @Autowired
    private ActivationCodeService activationCodeService;

    @RequestMapping(value = Uri.LOGIN, method = RequestMethod.GET)
    public ResponseEntity loginUser(@RequestParam("login") String login,
                                    @RequestParam("password") String password) {
        return userAuthService.authenticateUser(login, password);
    }

    @RequestMapping(value = Uri.REGISTER, method = RequestMethod.POST)
    public ResponseEntity registerUser(@RequestBody User user){
        return registrationValidator.validateUserToRegistration(user);
    }

    @RequestMapping(value = Uri.ACTIVATE, method = RequestMethod.GET)
    public ResponseEntity activateUser(@RequestParam("activationCode")String activationCode){
        return activationCodeService.activateUser(activationCode);
    }
}
