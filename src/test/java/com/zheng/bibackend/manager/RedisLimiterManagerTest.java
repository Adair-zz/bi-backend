package com.zheng.bibackend.manager;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/18/2023 - 16:53
 */
@SpringBootTest
class RedisLimiterManagerTest {
  
  @Resource
  private RedisLimiterManager redisLimiterManager;
  
  @Test
  void doRateLimit() {
    String userId = "1";
    for (int i = 0; i < 2; i++) {
      redisLimiterManager.doRateLimit(userId);
      System.out.println("Success: " + i);
    }
  }
}