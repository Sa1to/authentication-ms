package com.szymon.dao;

import com.szymon.domain.ActivationCode;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ActivationCodeDaoImpl extends AbstractDaoImpl<ActivationCode> implements ActivationCodeDao {
    @Autowired
    private Datastore datastore;

    private Query<ActivationCode> query;

    private FieldEnd fieldEnd;

    @Override
    public ActivationCode findByUserId(ObjectId userId) {
        query = datastore.find(ActivationCode.class);
        fieldEnd = query.criteria("userId");
        fieldEnd.equal(userId);
        return query.get();
    }
}
