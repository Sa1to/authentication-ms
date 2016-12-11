package com.szymon;

import com.szymon.dao.TokenDao;
import com.szymon.dao.TokenDaoImpl;
import com.szymon.domain.Token;
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
import org.mongodb.morphia.query.UpdateOperations;

public class TokenDaoTests {
    @Mock
    private Datastore datastore;

    @Mock
    private Token token;

    @Mock
    private Query<Token> query;

    @Mock
    private FieldEnd fieldEnd;

    @Mock
    private UpdateOperations updateOperations;

    @InjectMocks
    private TokenDao tokenDao = new TokenDaoImpl();

    private ObjectId testUserId = new ObjectId();
    private String testStringToken = "test";

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

    @Test
    public void findByStringTokenValue() {
        Mockito.stub(datastore.find(Token.class)).toReturn(query);
        Mockito.stub(query.criteria("token")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(testStringToken)).toReturn(token);

        tokenDao.findByStringTokenValue(testStringToken);

        Mockito.verify(datastore).find(Token.class);
        Mockito.verify(query).criteria("token");
        Mockito.verify(fieldEnd).equal(testStringToken);
        Mockito.verify(query).get();
    }

    @Test
    public void upDateToken() {
        String oldToken = "old";
        String newToken = "new";
        Token token = new Token(null, oldToken);

        Mockito.stub(datastore.createUpdateOperations(Token.class)).toReturn(updateOperations);
        Mockito.stub(updateOperations.set("token", newToken)).toReturn(updateOperations);

        tokenDao.updateToken(token, newToken);

        Mockito.verify(datastore).createUpdateOperations(Token.class);
        Mockito.verify(updateOperations).set("token", newToken);
        Mockito.verify(datastore).update(token, updateOperations);
    }
}
