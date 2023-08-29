package com.zheng.bibackend.exception;

import com.zheng.bibackend.common.BaseResponse;
import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author: Zheng Zhang
 * @Description Global Exception Handler.
 * @Created 04/27/2023 - 12:46
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  public BaseResponse<?> businessExceptionHandler(BusinessException e) {
    log.error("BusinessException", e);
    return ResultUtils.error(e.getCode(), e.getMessage());
  }
  
  @ExceptionHandler(RuntimeException.class)
  public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
    log.error("RuntimeException", e);
    return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "System error");
  }
}
