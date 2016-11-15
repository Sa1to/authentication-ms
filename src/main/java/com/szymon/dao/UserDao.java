package com.szymon.dao;

import com.szymon.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    void saveWithHashedPassword(User user);

//    List<User> findByNameAndSurname(String name, String surname);

    User findByLogin(String login);
}
