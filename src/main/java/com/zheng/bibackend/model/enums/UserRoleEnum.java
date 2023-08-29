package com.zheng.bibackend.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Zheng Zhang
 * @Description Role enum
 * @Created 04/23/2023 - 15:51
 */
public enum UserRoleEnum {
  
  USER("user", "user"),
  ADMIN("admin", "admin"),
  BAN("banned", "banned");
  
  private final String role;
  
  private final String value;
  
  UserRoleEnum(String role, String value) {
    this.role = role;
    this.value = value;
  }
  
  /**
   * Get list of role value.
   *
   * @return list of role value
   */
  public static List<String> getValues() {
    return Arrays.stream(values()).map(role -> role.value).collect(Collectors.toList());
  }
  
  /**
   * Get role enum by a value.
   *
   * @param value role value
   * @return UserRoleEnum obj
   */
  public static UserRoleEnum getEnumByValue(String value) {
    if (ObjectUtils.isEmpty(value)) {
      return null;
    }
    for (UserRoleEnum anEnum : UserRoleEnum.values()) {
      if (anEnum.value.equals(value)) {
        return anEnum;
      }
    }
    return null;
  }
  
  public String getValue() {
    return value;
  }
  
  public String getRole() {
    return role;
  }
}
