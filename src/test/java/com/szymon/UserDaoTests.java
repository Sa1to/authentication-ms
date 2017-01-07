package com.szymon;

import com.szymon.Texts.RoleEnum;
import com.szymon.dao.UserDao;
import com.szymon.dao.UserDaoImpl;
import com.szymon.domain.User;
import org.apache.commons.lang.RandomStringUtils;
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

public class UserDaoTests {

    @Mock
    private Query query;

    @Mock
    private FieldEnd fieldEnd;

    @Mock
    private UpdateOperations updateOperations;

    @Mock
    private Datastore datastore;

    @InjectMocks
    private UserDao userDao = new UserDaoImpl();

    private User user;
    private String password = RandomStringUtils.random(7);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User("jankowalski", "Jan", "Kowalski", password, RoleEnum.USER, false);
    }

    @Test
    public void saveWithHashedPassword() {
        Mockito.stub(datastore.save(user)).toReturn(null);
        userDao.save(user);
        Mockito.verify(datastore).save(user);
    }

    @Test
    public void findByLogin() {
        Mockito.stub(datastore.find(User.class)).toReturn(query);
        Mockito.stub(query.criteria("login")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(user.getLogin())).toReturn(user);

        userDao.findByLogin(user.getLogin());

        Mockito.verify(datastore).find(User.class);
        Mockito.verify(query).criteria("login");
        Mockito.verify(fieldEnd).equal(user.getLogin());
        Mockito.verify(query).get();
    }

    @Test
    public void findById() {
        Mockito.stub(datastore.find(User.class)).toReturn(query);
        Mockito.stub(query.criteria("_id")).toReturn(fieldEnd);
        Mockito.stub(fieldEnd.equal(user.getId())).toReturn(user);

        userDao.findById(user.getId());

        Mockito.verify(datastore).find(User.class);
        Mockito.verify(query).criteria("_id");
        Mockito.verify(fieldEnd).equal(user.getId());
        Mockito.verify(query).get();
    }

    @Test
    public void updateActivation() {

        Mockito.stub(datastore.createUpdateOperations(User.class)).toReturn(updateOperations);
        Mockito.stub(updateOperations.set("active", true)).toReturn(updateOperations);

        userDao.updateActivation(user, true);

        Mockito.verify(datastore).createUpdateOperations(User.class);
        Mockito.verify(updateOperations).set("active", true);
        Mockito.verify(datastore).update(user, updateOperations);
    }
}
