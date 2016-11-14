package com.szymon.dao;

import com.szymon.entity.Token;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenDaoImpl implements TokenDao {
    @Autowired
    private Datastore datastore;

    @Override
    public void delete(Token token) {
            datastore.delete(token);
    }

    @Override
    public void save(Token token) {
        datastore.save(token);
    }
}
