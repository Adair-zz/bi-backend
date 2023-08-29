package com.zheng.bibackend.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/29/2023 - 21:50
 */
@Aspect
@Slf4j
@Component
public class LogInterceptor {
  
  @Around("execution(* com.zheng.bibackend.controller.*.*(..))")
  public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
    // timing
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    // Get request
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
    // generate unique request id
    String requestId = UUID.randomUUID().toString();
    String url = httpServletRequest.getRequestURI();
    // get request params
    Object[] args = point.getArgs();
    String reqParam = "[" + StringUtils.join(args, ", ") + "]";
    // request log
    log.info("Request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
        httpServletRequest.getRemoteHost(), reqParam);
    // Execute the original method
    Object result = point.proceed();
    // output log
    stopWatch.stop();
    long totalTimeMillis = stopWatch.getTotalTimeMillis();
    log.info("Request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
    return result;
  }
}
