package com.zheng.bibackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zheng.bibackend.model.entity.Chart;
import com.zheng.bibackend.service.ChartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 09/04/2023 - 15:50
 */
@Slf4j
@Component
public class PreCacheJob {
  
  @Resource
  private ChartService chartService;
  
  @Resource
  private RedisTemplate redisTemplate;
  
  @Resource
  private RedissonClient redissonClient;
  
  private List<Long> mainUserList = Arrays.asList(1691200092494454786L);
  
  @Scheduled(cron = "0 55 17 * * *")
  public void doCacheRecommendedCharts() {
    RLock lock = redissonClient.getLock("zheng:precachejob:docache:lock");
    try {
      // only allow one thread to get the lock
      if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
        log.info("getLock:" + Thread.currentThread().getId());
        for (Long userId : mainUserList) {
          QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("userId", userId)
              .isNotNull("genChart")
              .isNotNull("genResult")
              .ne("genChart", "")
              .ne("genResult", "")
              .eq("status", "success");
          Page<Chart> chartPage = chartService.page(new Page<>(1, 20), queryWrapper);
          String redisKey = String.format("zheng:user:chart:recommend:%s", userId);
          ValueOperations valueOperations = redisTemplate.opsForValue();
          try {
            valueOperations.set(redisKey, chartPage, 300000, TimeUnit.MILLISECONDS);
          } catch (Exception e) {
            log.error("redis set key error", e);
          }
        }
      }
    } catch (InterruptedException e) {
      log.error("doCacheRecommendedCharts error", e);
    } finally {
      if (lock.isHeldByCurrentThread()) {
        log.info("unlock:" + Thread.currentThread().getId());
        lock.unlock();
      }
    }
  }
}
