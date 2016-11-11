package com.szymon.Entity;


import com.szymon.Texts.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @GeneratedValue
    private String id;
    private String name;
    private String surname;
    private RoleEnum role;
    
    public UserEntity(String name, String surname, RoleEnum role) {
        this.name = name;
        this.surname = surname;
        this.role = role;
    }
}
