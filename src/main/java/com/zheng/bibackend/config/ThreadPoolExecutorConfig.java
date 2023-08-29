package com.zheng.bibackend.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/18/2023 - 18:39
 */
@Configuration
public class ThreadPoolExecutorConfig {

  @Bean
  public ThreadPoolExecutor threadPoolExecutor() {
    ThreadFactory threadFactory = new ThreadFactory() {
      int count = 1;
      
      @Override
      public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("Thread" + count);
        count++;
        return thread;
      }
    };
    
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 100,
        TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), threadFactory);
    
    return threadPoolExecutor;
    
  }
  
}
