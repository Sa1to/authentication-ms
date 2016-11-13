package com.szymon.dao;

import com.szymon.entity.User;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private Datastore datastore;

    public void saveWithHashedPassword(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        datastore.save(user);
    }

    public void delete(User user){
        datastore.delete(user);
    }

    @Override
    public User findByLogin(String login) {
        Query<User> query = datastore.find(User.class);
        query.criteria("login").equal(login);
        return query.get();
    }

    public List<User> findByNameAndSurname(String name, String surname) {
        Query<User> query = datastore.find(User.class);
        query.or(
                query.criteria("name").equal(name),
                query.criteria("surname").equal(surname)
        );
        return query.asList();
    }

    private String hashPassword(String passwordToHash) {
        return BCrypt.hashpw(passwordToHash, BCrypt.gensalt());
    }
}
