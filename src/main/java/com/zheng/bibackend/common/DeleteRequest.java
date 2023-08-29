package com.zheng.bibackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/25/2023 - 20:51
 */
@Data
public class DeleteRequest implements Serializable {
  
  /**
   * id
   */
  private Long id;
  
  private static final long serialVersionUID = 1L;
}
