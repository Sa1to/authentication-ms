package com.szymon.entity;


import com.szymon.Texts.RoleEnum;
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
public class User {

    @Id
    private ObjectId id;
    private String login;
    private String name;
    private String surname;
    private String password;
    private RoleEnum role;

    public User(String login, String name, String surname, String password, RoleEnum role) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.role = role;
    }
}
