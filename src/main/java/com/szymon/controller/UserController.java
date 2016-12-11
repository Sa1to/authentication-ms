package com.szymon.controller;

import com.szymon.Texts.Uri;
import com.szymon.domain.Credentials;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.RegistrationValidator;
import com.szymon.service.TokenRenewService;
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

    @Autowired
    private TokenRenewService tokenRenewService;

    @RequestMapping(value = Uri.LOGIN, method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity loginUser(@RequestBody Credentials credentials) {
        return userAuthService.authenticateUserBaseOnCredentials(credentials.getLogin(), credentials.getPassword());
    }

    @RequestMapping(value = Uri.LOGOUT, method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity logoutUser(@RequestHeader("Authorization") String token) {
        return userAuthService.validateAndRemoveToken(token);
    }

    @RequestMapping(value = Uri.REGISTER, method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity registerUser(@RequestBody User user) {
        return registrationValidator.validateUserToRegistration(user);
    }

    @RequestMapping(value = Uri.ACTIVATE, method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity activateUser(@RequestParam("activationCode") String activationCode) {
        return activationCodeService.activateUser(activationCode);
    }

    @RequestMapping(value = Uri.AUTHENTICATE, method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity authenticateUser(@RequestHeader("Authorization") String token) {
        return userAuthService.authenticateUserBaseOnToken(token);
    }

    @RequestMapping(value = Uri.RENEW, method = RequestMethod.GET)
    public ResponseEntity renewToken(@RequestHeader("Authorization") String token) {
        return tokenRenewService.renewTokenExpirationDate(token);
    }
}
