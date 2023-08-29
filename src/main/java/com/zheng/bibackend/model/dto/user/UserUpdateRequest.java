package com.zheng.bibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/25/2023 - 21:02
 */
@Data
public class UserUpdateRequest implements Serializable {
  
  /**
   * id
   */
  private Long id;
  
  /**
   * User name.
   */
  private String userName;
  
  /**
   * User avatar.
   */
  private String userAvatar;
  
  /**
   * User role: user/admin/ban
   */
  private String userRole;
  
  private static final long serialVersionUID = 1L;
}
