package com.zheng.bibackend.manager;

import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.exception.BusinessException;
import jakarta.annotation.Resource;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;



/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/18/2023 - 16:43
 */
@Service
public class RedisLimiterManager {
  
  @Resource
  private RedissonClient redissonClient;
  
  /**
   * 限流操作
   *
   * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
   */
  public void doRateLimit(String key) {
    // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
    RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
    rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
    // 每当一个操作来了后，请求一个令牌
    boolean canOp = rateLimiter.tryAcquire(1);
    if (!canOp) {
      throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
    }
  }
  
}