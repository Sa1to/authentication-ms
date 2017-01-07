package com.szymon.dao;

import com.szymon.domain.User;
import org.bson.types.ObjectId;

public interface UserDao extends AbstractDao<User> {
    User findByLogin(String login);

    User findById(ObjectId id);

    void updateActivation(User user, boolean activation);
}
