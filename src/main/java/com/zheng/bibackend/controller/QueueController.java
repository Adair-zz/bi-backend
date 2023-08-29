package com.zheng.bibackend.controller;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @Author: Zheng Zhang
 * @Description Only for testing the Thread Pool Executor.
 * @Created 08/18/2023 - 20:30
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({ "dev", "local" })
public class QueueController {
  
  @Resource
  private ThreadPoolExecutor threadPoolExecutor;
  
  @GetMapping("/add")
  public void add(String name) {
    CompletableFuture.runAsync(() -> {
      log.info("Task Processing: " + name + "，Executor: " + Thread.currentThread().getName());
      try {
        Thread.sleep(600000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, threadPoolExecutor);
  }
  
  @GetMapping("/get")
  public String get() {
    Map<String, Object> map = new HashMap<>();
    int size = threadPoolExecutor.getQueue().size();
    map.put("Queue Size: ", size);
    long taskCount = threadPoolExecutor.getTaskCount();
    map.put("Total Task", taskCount);
    long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
    map.put("Completed Task Count: ", completedTaskCount);
    int activeCount = threadPoolExecutor.getActiveCount();
    map.put("Active Count: ", activeCount);
    return JSONUtil.toJsonStr(map);
  }
  
}
