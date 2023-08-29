package com.zheng.bibackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Zheng Zhang
 * @Description Desensitized user info.
 * @Created 04/23/2023 - 15:18
 */
@Data
public class UserVO implements Serializable {
  
  /**
   * user id.
   */
  private Long id;
  
  /**
   * User name;
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
  
  /**
   * Create time.
   */
  private Date createTime;
  
  private static final long serialVersionUID = 2L;
}
