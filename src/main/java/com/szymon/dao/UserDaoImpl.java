package com.szymon.dao;

import com.szymon.domain.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends AbstractDaoImpl<User> implements UserDao {

    @Autowired
    private Datastore datastore;

    private Query<User> query;

    private FieldEnd fieldEnd;

    private UpdateOperations updateOperations;

    public void save(User user) {
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

    @Override
    public User findById(ObjectId id) {
        query = datastore.find(User.class);
        fieldEnd = query.criteria("_id");
        fieldEnd.equal(id);
        return query.get();
    }

    @Override
    public void updateActivation(User user, boolean activation) {
        updateOperations = datastore.createUpdateOperations(User.class);
        updateOperations.set("active", activation);
        datastore.update(user, updateOperations);
    }

    private String hashPassword(String passwordToHash) {
        return BCrypt.hashpw(passwordToHash, BCrypt.gensalt());
    }
}
