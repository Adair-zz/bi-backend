package com.zheng.bibackend.exception;

import com.zheng.bibackend.common.ErrorCode;

/**
 * @Author: Zheng Zhang
 * @Description Customized exception.
 * @Created 04/27/2023 - 12:38
 */
public class BusinessException extends RuntimeException{
  
  /**
   * Error Code
   */
  private final int code;
  
  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }
  
  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }
  
  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.code = errorCode.getCode();
  }
  
  public int getCode() {
    return code;
  }
}
