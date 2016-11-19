package com.szymon.dao;

import com.szymon.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    void saveWithHashedPassword(User user);

//    List<User> findByNameAndSurname(String name, String surname);

    User findByLogin(String login);
}
