package com.zheng.bibackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/29/2023 - 19:36
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
  
  /**
   * There must have a user role.
   *
   * @return
   */
  String mustRole() default "";
}
