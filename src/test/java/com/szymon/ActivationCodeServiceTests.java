package com.szymon;

import com.szymon.Texts.RoleEnum;
import com.szymon.dao.ActivationCodeDao;
import com.szymon.domain.User;
import com.szymon.service.ActivationCodeService;
import com.szymon.service.ActivationCodeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;

public class ActivationCodeServiceTests {

    @Mock
    private ActivationCodeDao activationCodeDao;

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
        activationCodeService.createAndSave(userToRegister);

        Mockito.verify(activationCodeDao).save(any());
    }

}
