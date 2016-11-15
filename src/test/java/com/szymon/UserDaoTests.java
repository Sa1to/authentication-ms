package com.szymon;

import com.szymon.dao.UserDaoImpl;
import com.szymon.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;

public class UserDaoTests {

    @Mock
    private Query query;

    @Mock
    private FieldEnd fieldEnd;

    @Mock
    private Datastore datastore;

    @InjectMocks
    private UserDaoImpl userDao;

    @Mock
    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSavingWithHashedPassword() {
        userDao.saveWithHashedPassword(user);
        Mockito.verify(datastore).save(user);
    }

    @Test
    public void testFindByLogin() {
        Mockito.stub(datastore.find(User.class)).toReturn(query);
        Mockito.stub(query.criteria("login")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(user.getLogin())).toReturn(user);
        userDao.findByLogin(user.getLogin());
        Mockito.verify(datastore).find(User.class);
        Mockito.verify(query).criteria("login");
        Mockito.verify(fieldEnd).equal(user.getLogin());
        Mockito.verify(query).get();

    }
}