package com.szymon.dao;

public interface AbstractDao<T> {
    void save(T t);

    void delete(T t);
}
