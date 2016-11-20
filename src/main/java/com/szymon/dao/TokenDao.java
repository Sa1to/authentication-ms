package com.szymon.dao;

import com.szymon.domain.Token;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

public interface TokenDao extends AbstractDao<Token>{
    Token findByUserId(ObjectId userId);
}
