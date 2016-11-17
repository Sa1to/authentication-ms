package com.szymon;

import com.szymon.dao.TokenDao;
import com.szymon.dao.TokenDaoImpl;
import com.szymon.entity.Token;
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

public class TokenDaoTests {
    @Mock
    private Datastore datastore;

    @Mock
    private Token token;

    @Mock
    private Query<Token> query;

    @Mock
    private FieldEnd fieldEnd;

    @InjectMocks
    private TokenDao tokenDao = new TokenDaoImpl();

    private ObjectId testUserId = new ObjectId();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveTokenTest() {
        tokenDao.save(token);
        Mockito.verify(datastore).save(token);
    }

    @Test
    public void deleteTokenTest() {
        tokenDao.delete(token);
        Mockito.verify(datastore).delete(token);
    }

    @Test
    public void getTokenByUserId() {
        Mockito.stub(datastore.find(Token.class)).toReturn(query);
        Mockito.stub(query.criteria("userId")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(testUserId)).toReturn(token);
        tokenDao.findByUserId(testUserId);
        Mockito.verify(datastore).find(Token.class);
        Mockito.verify(query).criteria("userId");
        Mockito.verify(fieldEnd).equal(testUserId);
        Mockito.verify(query).get();
    }
}
