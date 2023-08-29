package com.zheng.bibackend.exception;

import com.zheng.bibackend.common.ErrorCode;

/**
 * @Author: Zheng Zhang
 * @Description Throw exception utils
 * @Created 05/04/2023 - 19:28
 */
public class ThrowUtils {
  
  /**
   *
   * @param condition
   * @param runtimeException
   */
  public static void throwIf(boolean condition, RuntimeException runtimeException) {
    if (condition) {
      throw runtimeException;
    }
  }
  
  /**
   *
   * @param condition
   * @param errorCode
   */
  public static void throwIf(boolean condition, ErrorCode errorCode) {
    throwIf(condition, new BusinessException(errorCode));
  }
  
  /**
   *
   * @param condition
   * @param errorCode
   * @param message
   */
  public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
    throwIf(condition, new BusinessException(errorCode, message));
  }
}
