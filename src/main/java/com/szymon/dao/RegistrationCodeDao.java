package com.szymon.dao;

import com.szymon.domain.RegistrationCode;
import org.bson.types.ObjectId;

public interface RegistrationCodeDao extends AbstractDao<RegistrationCode> {

    RegistrationCode findByUserId(ObjectId userId);
}
