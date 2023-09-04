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
   * rate limit.
   *
   * @param key rate limiter id, which is associated with user id
   */
  public void doRateLimit(String key) {
    // allow 1 request in 15 seconds
    RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
    rateLimiter.trySetRate(RateType.OVERALL, 1, 15, RateIntervalUnit.SECONDS);

    boolean canOp = rateLimiter.tryAcquire(1);
    if (!canOp) {
      throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
    }
  }
  
}
