package com.zheng.bibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/25/2023 - 19:21
 */
@Data
public class UserAddRequest implements Serializable {
  
  /**
   * User name.
   */
  private String userName;
  
  /**
   * User account.
   */
  private String userAccount;
  
  /**
   * User Avatar.
   */
  private String userAvatar;
  
  /**
   * User role: user, admin.
   */
  private String userRole;
  
  private static final long serialVersionUID = 1L;
}
