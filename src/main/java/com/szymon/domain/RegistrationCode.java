package com.szymon.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RegistrationCode {

    @Id
    private ObjectId id;
    private ObjectId userId;
    private String code;

    public RegistrationCode(ObjectId userId, String code){
        this.userId = userId;
        this.code = code;
    }
}
