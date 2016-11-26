package com.szymon;

import com.szymon.Texts.Responses;
import com.szymon.Texts.RoleEnum;
import com.szymon.dao.ActivationCodeDao;
import com.szymon.dao.UserDao;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.ActivationCodeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;

public class ActivationCodeServiceTests {

    @Mock
    private ActivationCodeDao activationCodeDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ActivationCodeService activationCodeService = new ActivationCodeServiceImpl();
    private User userToRegister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userToRegister = new User("testlogin123", "testName", "testSurname", "testPassw0rd", RoleEnum.USER, false);
    }

    @Test
    public void createAndSaveTest() {
        ActivationCode code = activationCodeService.createAndSave(userToRegister);

        Mockito.verify(activationCodeDao).save(code);
        assertNotNull(code.getCode());
    }

    @Test
    public void activateUserTest() {
        String activationCodeString = "testActivationCode";
        ActivationCode activationCode = new ActivationCode();

        Mockito.stub(activationCodeDao.findByCode(activationCodeString)).toReturn(activationCode);
        Mockito.stub(userDao.findById(activationCode.getUserId())).toReturn(userToRegister);

        ResponseEntity response = activationCodeService.activateUser(activationCodeString);

        Mockito.verify(activationCodeDao).findByCode(activationCodeString);
        Mockito.verify(userDao).findById(activationCode.getUserId());
        Mockito.verify(userDao).updateActivation(userToRegister, true);

        assertEquals(Responses.USER_ACTIVATED, response.getBody().toString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void tryToActivateUserWithIncorrectCode() {
        String activationCodeString = "incorrectCode";

        Mockito.stub(activationCodeDao.findByCode(activationCodeString)).toReturn(null);

        ResponseEntity response = activationCodeService.activateUser(activationCodeString);

        Mockito.verify(activationCodeDao).findByCode(activationCodeString);
        Mockito.verify(userDao, Mockito.never()).findById(any());

        assertEquals(Responses.INCORRECT_ACTIVATION_CODE, response.getBody().toString());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
