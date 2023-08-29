package com.zheng.bibackend.common;

/**
 * @Author: Zheng Zhang
 * @Description Customized Error code.
 * @Created 04/25/2023 - 22:38
 */
public enum ErrorCode {
  SUCCESS(0, "Success"),
  PARAMS_ERROR(40000, "Parameters Error"),
  NOT_LOGIN_ERROR(40100, "Not Login"),
  NO_AUTH_ERROR(40101, "No Authentication"),
  NOT_FOUND_ERROR(40400, "Not Found"),
  FORBIDDEN_ERROR(40300, "No Access"),
  SYSTEM_ERROR(50000, "System Internal Error"),
  OPERATION_ERROR(50001, "Operation Fail"),
  TOO_MANY_REQUEST(429, "Too Many Request");
  
  /**
   * status code.
   */
  private final int code;
  
  /**
   * message.
   */
  private final String message;
  
  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
  
  public int getCode() {
    return code;
  }
  
  public String getMessage() {
    return message;
  }
}
