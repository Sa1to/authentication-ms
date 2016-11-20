package com.szymon.dao;

import com.szymon.domain.Token;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TokenDaoImpl extends AbstractDao<Token> implements TokenDao {
    @Autowired
    private Datastore datastore;

    private Query<Token> query;

    private FieldEnd fieldEnd;
    
    @Override
    public Token findByUserId(ObjectId userId) {
        query = datastore.find(Token.class);
        fieldEnd = query.criteria("userId");
        fieldEnd.equal(userId);
        return query.get();
    }
}
