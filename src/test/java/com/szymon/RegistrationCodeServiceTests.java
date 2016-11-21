package com.szymon;

import com.szymon.Texts.RoleEnum;
import com.szymon.dao.RegistrationCodeDao;
import com.szymon.domain.User;
import com.szymon.service.RegistrationCodeService;
import com.szymon.service.RegistrationCodeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;

public class RegistrationCodeServiceTests {

    @Mock
    private RegistrationCodeDao registrationCodeDao;

    @InjectMocks
    private RegistrationCodeService registrationCodeService = new RegistrationCodeServiceImpl();
    private User userToRegister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userToRegister = new User("testlogin123", "testName", "testSurname", "testPassw0rd", RoleEnum.USER, false);
    }

    @Test
    public void createAndSaveTest() {
        registrationCodeService.createAndSave(userToRegister);

        Mockito.verify(registrationCodeDao).save(any());
    }

//    @Test


}
