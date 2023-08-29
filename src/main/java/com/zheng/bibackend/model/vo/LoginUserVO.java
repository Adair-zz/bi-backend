package com.zheng.bibackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Zheng Zhang
 * @Description Desensitized current login user info.
 * @Created 04/23/2023 - 15:18
 */
@Data
public class LoginUserVO implements Serializable {
  /**
   * user id.
   */
  private Long id;
  
  /**
   * User name.
   */
  private String userName;
  
  /**
   * User Avatar.
   */
  private String userAvatar;

  /**
   * User role: user/admin/ban
   */
  private String userRole;
  
  /**
   * Create Time.
   */
  private Date createTime;
  
  /**
   * Update time.
   */
  private Date updateTime;
  
  private static final long serialVersionUID = 3L;
}
