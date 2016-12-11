package com.szymon.dao;

import com.szymon.domain.Token;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

public interface TokenDao extends AbstractDao<Token>{
    Token findByUserId(ObjectId userId);

    Token findByStringTokenValue(String token);

    void updateToken(Token token, String renewedToken);
}
