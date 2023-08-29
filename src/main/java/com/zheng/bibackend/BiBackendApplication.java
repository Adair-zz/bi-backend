package com.zheng.bibackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zheng.bibackend.mapper")
@SpringBootApplication
public class BiBackendApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(BiBackendApplication.class, args);
  }
  
}
