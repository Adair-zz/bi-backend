package com.zheng.bibackend.common;

/**
 * @Author: Zheng Zhang
 * @Description Result response utils.
 * @Created 04/26/2023 - 22:44
 */
public class ResultUtils {
  
  /**
   * Success.
   *
   * @param data
   * @param <T>
   * @return
   */
  public static <T> BaseResponse<T> success(T data) {
    return new BaseResponse<>(0, data, "Success");
  }
  
  /**
   * Failed.
   *
   * @param errorCode
   * @return
   */
  public static BaseResponse error(ErrorCode errorCode) {
    return new BaseResponse<>(errorCode);
  }
  
  /**
   * Failed.
   *
   * @param code
   * @param message
   * @return
   */
  public static BaseResponse error(int code, String message) {
    return new BaseResponse(code, null, message);
  }
  
  /**
   * Failed.
   *
   * @param errorCode
   * @param message
   * @return
   */
  public static BaseResponse error(ErrorCode errorCode, String message) {
    return new BaseResponse<>(errorCode.getCode(), null, message);
  }
}
