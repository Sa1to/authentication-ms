package com.szymon.dao;

import com.szymon.domain.Token;
import com.szymon.domain.User;
import org.springframework.stereotype.Repository;

public interface UserDao extends AbstractDao<User> {
    User findByLogin(String login);
}
