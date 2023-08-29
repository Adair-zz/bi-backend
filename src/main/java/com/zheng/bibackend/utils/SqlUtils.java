package com.zheng.bibackend.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/23/2023 - 16:27
 */
public class SqlUtils {
  
  /**
   * Validate sort field (prevent sql injection).
   *
   * @param sortField
   * @return
   */
  public static boolean validSortField(String sortField) {
    if (StringUtils.isBlank(sortField)) {
      return false;
    }
    return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
  }
}
