package com.szymon;

import com.szymon.dao.ActivationCodeDao;
import com.szymon.dao.ActivationCodeDaoImpl;
import com.szymon.domain.ActivationCode;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;

public class ActivationDaoTests {
    @Mock
    private Datastore datastore;

    @Mock
    private ActivationCode activationCode;

    @Mock
    private Query<ActivationCode> query;

    @Mock
    private FieldEnd fieldEnd;

    @InjectMocks
    private ActivationCodeDao activationCodeDao = new ActivationCodeDaoImpl();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findByUserId() {
        ObjectId testUserId = new ObjectId();

        Mockito.stub(datastore.find(ActivationCode.class)).toReturn(query);
        Mockito.stub(query.criteria("userId")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(testUserId)).toReturn(activationCode);

        activationCodeDao.findByUserId(testUserId);

        Mockito.verify(datastore).find(ActivationCode.class);
        Mockito.verify(query).criteria("userId");
        Mockito.verify(fieldEnd).equal(testUserId);
        Mockito.verify(query).get();
    }

    @Test
    public void findByCode() {
        String code = "testCode";
        Mockito.stub(datastore.find(ActivationCode.class)).toReturn(query);
        Mockito.stub(query.criteria("code")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(code)).toReturn(activationCode);

        activationCodeDao.findByCode(code);

        Mockito.verify(datastore).find(ActivationCode.class);
        Mockito.verify(query).criteria("code");
        Mockito.verify(fieldEnd).equal(code);
        Mockito.verify(query).get();
    }

}
