package com.szymon;

import com.szymon.Entity.UserEntity;
import com.szymon.Texts.RoleEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationMsApplicationTests {

	@Autowired
	UserEntity userEntity;

	@Test
	public void contextLoads() {
	}

	@Test
	public void registerNewUser(){
	UserEntity user = new UserEntity("Jan", "Kowalski", RoleEnum.USER);

	}

}
