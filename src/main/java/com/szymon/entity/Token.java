package com.szymon.entity;

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
public class Token {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private String token;

    public Token(ObjectId userId, String token) {
        this.userId = userId;
        this.token = token;
    }

}
