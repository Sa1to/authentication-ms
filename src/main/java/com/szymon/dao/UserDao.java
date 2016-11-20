package com.szymon.dao;

import com.szymon.domain.User;
import org.springframework.stereotype.Repository;

public interface UserDao {
    void save(User user);

    User findByLogin(String login);
}
