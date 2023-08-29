package com.zheng.bibackend.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 07/25/2023 - 21:42
 */
@SpringBootTest
public class RedissonTest {
  
  @Resource
  private RedissonClient redissonClient;
  
  @Test
  void redissonTest() {
    RList<String> rList = redissonClient.getList("test-redisson");
    rList.add("1271470005");
    rList.add("2843517195");
    Assertions.assertTrue("1271470005".equals(rList.remove(0)));
    Assertions.assertTrue("2843517195".equals(rList.remove(0)));
  }
}
