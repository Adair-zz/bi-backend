package com.zheng.bibackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 07/25/2023 - 19:41
 */
@ConfigurationProperties(prefix = "spring.data.redis")
@Configuration
@Data
public class RedissonConfig {
  private Integer database;
  
  private String host;
  
  private Integer port;
  
  private String password;
  
  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setDatabase(database)
        .setAddress("redis://" + host + ":" + port);
    RedissonClient redissonClient = Redisson.create(config);
    return redissonClient;
  }
}
