package com.szymon.dao;

import com.szymon.domain.ActivationCode;
import org.bson.types.ObjectId;

public interface ActivationCodeDao extends AbstractDao<ActivationCode> {

    ActivationCode findByUserId(ObjectId userId);
}
