package com.zheng.bibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 05/22/2023 - 20:12
 */
@Data
public class UserRegisterRequest implements Serializable {
  
  private String userAccount;
  
  private String userPassword;
  
  private String confirmPassword;
  
  private static final long serialVersionUID = 1L;
}
