package com.zheng.bibackend.common;

import com.zheng.bibackend.constant.CommonConstant;
import lombok.Data;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/24/2023 - 16:17
 */
@Data
public class PageRequest {
  
  /**
   * Current page number.
   */
  private long currentPage = 1;
  
  /**
   * Page size.
   */
  private long pageSize = 10;
  
  /**
   * Sort field.
   */
  private String sortField;
  
  /**
   * Sort order.
   */
  private String sortOrder = CommonConstant.ASCENDING_ORDER;
}
