package com.szymon.Entity;


import com.szymon.Texts.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

/**
 * Created by szpaw on 10.11.2016.
 */
@Entity
@Getter
@Setter
public class UserEntity {

    @GeneratedValue
    private String id;
    private String name;
    private String surname;
    private RoleEnum role;

}
