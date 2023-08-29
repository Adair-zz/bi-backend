package com.zheng.bibackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/26/2023 - 22:27
 */
@Data
public class BaseResponse<T> implements Serializable {
  
  private int code;
  
  private T data;
  
  private String message;
  
  public BaseResponse(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }
  
  public BaseResponse(int code, T data) {
    this(code, data, "");
  }
  
  public BaseResponse(ErrorCode errorCode) {
    this(errorCode.getCode(), null, errorCode.getMessage());
  }
}
