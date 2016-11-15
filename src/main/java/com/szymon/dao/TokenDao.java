package com.szymon.dao;

import com.szymon.entity.Token;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenDao {
    void delete(Token token);
    void save(Token token);
}