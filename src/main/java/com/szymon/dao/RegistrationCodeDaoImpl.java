package com.szymon.dao;

import com.szymon.domain.RegistrationCode;
import com.szymon.domain.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RegistrationCodeDaoImpl extends AbstractDaoImpl<RegistrationCode> implements RegistrationCodeDao{
    @Autowired
    private Datastore datastore;

    private Query<RegistrationCode> query;

    private FieldEnd fieldEnd;

    @Override
    public RegistrationCode findByUserId(ObjectId userId) {
        query = datastore.find(RegistrationCode.class);
        fieldEnd = query.field("userId");
        fieldEnd.equal(userId);
        return query.get();
    }
}
