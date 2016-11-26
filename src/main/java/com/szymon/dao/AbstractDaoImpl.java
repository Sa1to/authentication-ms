package com.szymon.dao;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDaoImpl<T> implements AbstractDao<T> {
    @Autowired
    private Datastore datastore;

    @Override
    public void save(T t) {
        datastore.save(t);
    }

    @Override
    public void delete(T t) {
        datastore.delete(t);
    }
}
