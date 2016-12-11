package com.szymon.dao;

import com.szymon.domain.Token;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TokenDaoImpl extends AbstractDaoImpl<Token> implements TokenDao {
    @Autowired
    private Datastore datastore;

    private UpdateOperations updateOperations;

    private Query<Token> query;

    private FieldEnd fieldEnd;

    @Override
    public Token findByUserId(ObjectId userId) {
        query = datastore.find(Token.class);
        fieldEnd = query.criteria("userId");
        fieldEnd.equal(userId);
        return query.get();
    }

    @Override
    public Token findByStringTokenValue(String token) {
        query = datastore.find(Token.class);
        fieldEnd = query.criteria("token");
        fieldEnd.equal(token);
        return query.get();
    }

    @Override
    public void updateToken(Token token, String renewedToken) {
        updateOperations = datastore.createUpdateOperations(Token.class);
        updateOperations.set("token", renewedToken);
        datastore.update(token, updateOperations);
    }
}
