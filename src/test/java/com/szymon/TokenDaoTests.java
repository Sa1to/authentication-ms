package com.szymon;

import com.szymon.dao.TokenDaoImpl;
import com.szymon.entity.Token;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mongodb.morphia.Datastore;

public class TokenDaoTests {
    @Mock
    private Datastore datastore;

    @InjectMocks
    private TokenDaoImpl tokenDao;

    @Mock
    private Token token;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveTokenTest(){
        tokenDao.save(token);
        Mockito.verify(datastore).save(token);
    }

    @Test
    public void deleteTokenTest(){
        tokenDao.delete(token);
        Mockito.verify(datastore).delete(token);
    }
}
