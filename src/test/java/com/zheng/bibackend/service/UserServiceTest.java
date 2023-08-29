package com.zheng.bibackend.service;

import com.zheng.bibackend.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/22/2023 - 17:24
 */
@SpringBootTest
class UserServiceTest {
  @Resource
  private UserService userService;
  
  @Test
  void addUserToDBTest() {
    User newUser = new User();
    newUser.setUserAccount("1271470005" + new Random().nextInt(10));
    newUser.setUserPassword("123456789");
    newUser.setUserName("Zheng Zhang");
    newUser.setUserAvatar("https://lh3.google.com/u/0/ogw/AGvuzYYmW7xL5Fq6n9VJNGhYWKoAE3J2IvguawFL30Y=s32-c-mo");
    newUser.setUserRole("user");
  
    boolean isSave = userService.save(newUser);
    Assertions.assertTrue(isSave);
  }
}