package com.zheng.bibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/25/2023 - 20:28
 */
@Data
public class UserLoginRequest implements Serializable {
  
  private String userAccount;
  
  private String userPassword;
}
