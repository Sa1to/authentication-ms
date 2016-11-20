package com.szymon.dao;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao<T> {
    @Autowired
    private Datastore datastore;

    public void save(T t) {
        datastore.save(t);
    }

    public void delete(T t) {
       datastore.delete(t);
    }
}
