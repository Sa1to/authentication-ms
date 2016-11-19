package com.szymon.domain;


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
    private boolean active;

    public User(String login, String name, String surname, String password, RoleEnum role, boolean active) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.role = role;
        this.active = active;
    }
}
