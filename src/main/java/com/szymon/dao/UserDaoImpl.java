package com.szymon.dao;

import com.szymon.domain.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private Datastore datastore;

    private Query<User> query;

    private FieldEnd fieldEnd;

    public void saveWithHashedPassword(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        datastore.save(user);
    }

    @Override
    public User findByLogin(String login) {
        query = datastore.find(User.class);
        fieldEnd = query.criteria("login");
        fieldEnd.equal(login);
        return query.get();
    }

    private String hashPassword(String passwordToHash) {
        return BCrypt.hashpw(passwordToHash, BCrypt.gensalt());
    }
}
