package com.szymon.service;

import com.szymon.dao.ActivationCodeDao;
import com.szymon.domain.ActivationCode;
import com.szymon.domain.User;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    @Autowired
    private ActivationCodeDao activationCodeDao;

    @Override
    public void createAndSave(User user) {
        activationCodeDao.save(new ActivationCode(user.getId(), RandomStringUtils.random(20, true, true)));
    }
}
