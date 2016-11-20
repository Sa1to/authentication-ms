package com.szymon.service;

import com.szymon.dao.RegistrationCodeDao;
import com.szymon.domain.RegistrationCode;
import com.szymon.domain.User;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationCodeServiceImpl implements RegistrationCodeService {

    @Autowired
    private RegistrationCodeDao registrationCodeDao;

    @Override
    public void createAndSave(User user) {
        registrationCodeDao.save(new RegistrationCode(user.getId(), RandomStringUtils.random(20, true, true)));
    }
}
