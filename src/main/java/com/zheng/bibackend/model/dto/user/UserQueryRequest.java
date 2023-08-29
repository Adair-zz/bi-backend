package com.zheng.bibackend.model.dto.user;

import com.zheng.bibackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/23/2023 - 15:28
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
  
  /**
   * user id
   */
  private Long id;
  
  /**
   * User name.
   */
  private String userName;
  
  /**
   * User role: user/admin/ban
   */
  private String userRole;
  
  private static final long serialVersionUID = 1L;

}
