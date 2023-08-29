package com.zheng.bibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zheng.bibackend.annotation.AuthCheck;
import com.zheng.bibackend.common.BaseResponse;
import com.zheng.bibackend.common.DeleteRequest;
import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.common.ResultUtils;
import com.zheng.bibackend.constant.UserConstant;
import com.zheng.bibackend.exception.BusinessException;
import com.zheng.bibackend.exception.ThrowUtils;
import com.zheng.bibackend.model.dto.user.*;
import com.zheng.bibackend.model.entity.User;
import com.zheng.bibackend.model.vo.LoginUserVO;
import com.zheng.bibackend.model.vo.UserVO;
import com.zheng.bibackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 04/22/2023 - 19:15
 */
@RestController
@RequestMapping("/user")
public class UserController {
  
  @Resource
  private UserService userService;
  
  /**
   * User register.
   *
   * @param userRegisterRequest
   * @return
   */
  @RequestMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
    if (userRegisterRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
  
    String userAccount = userRegisterRequest.getUserAccount();
    String userPassword = userRegisterRequest.getUserPassword();
    String confirmPassword = userRegisterRequest.getConfirmPassword();
    if (StringUtils.isAnyBlank(userAccount, userPassword, confirmPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    long returnUserId = userService.userRegister(userAccount, userPassword, confirmPassword);
    return ResultUtils.success(returnUserId);
  }
  
  /**
   * User login.
   *
   * @param userLoginRequest
   * @param httpServletRequest
   * @return
   */
  @RequestMapping("/login")
  public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
    if (userLoginRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    
    String userAccount = userLoginRequest.getUserAccount();
    String userPassword = userLoginRequest.getUserPassword();
    if (StringUtils.isAnyBlank(userAccount, userPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, httpServletRequest);
    return ResultUtils.success(loginUserVO);
  }
  
  /**
   * User logout.
   *
   * @param httpServletRequest
   * @return
   */
  @PostMapping("/logout")
  public BaseResponse<Boolean> userLogout(HttpServletRequest httpServletRequest) {
    if (httpServletRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    boolean isLogout = userService.userLogout(httpServletRequest);
    return ResultUtils.success(isLogout);
  }
  
  
  /**
   * Get current login user.
   *
   * @param httpServletRequest
   * @return
   */
  @GetMapping("/get/login")
  public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest httpServletRequest) {
    User currentLoginUser = userService.getLoginUser(httpServletRequest);
    return ResultUtils.success(userService.getLoginUserVO(currentLoginUser));
  }
  
  /**
   * Add new user (only for admin, but not for user table).
   *
   * @param userAddRequest user add request
   * @return new user id
   */
  @PostMapping("/add")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
    if (userAddRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    
    User newUser = new User();
    BeanUtils.copyProperties(userAddRequest, newUser);
    boolean isAdd = userService.save(newUser);
    ThrowUtils.throwIf(!isAdd, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(newUser.getId());
  }
  
  /**
   * Delete User (only for admin).
   *
   * @param deleteRequest
   * @param httpServletRequest
   * @return
   */
  @PostMapping("/delete")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
    if (deleteRequest == null || deleteRequest.getId() <= 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    boolean isDelete = userService.removeById(deleteRequest.getId());
    return ResultUtils.success(isDelete);
  }
  
  /**
   * Update user (only for admin).
   *
   * @param userUpdateRequest
   * @param request
   * @return
   */
  @PostMapping("/update")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                          HttpServletRequest request) {
    if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User user = new User();
    BeanUtils.copyProperties(userUpdateRequest, user);
    boolean isUpdate = userService.updateById(user);
    ThrowUtils.throwIf(!isUpdate, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(true);
  }
  
  /**
   * Get user by id (only for admin).
   *
   * @param id
   * @param request
   * @return
   */
  @GetMapping("/get")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
    if (id <= 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User userById = userService.getById(id);
    ThrowUtils.throwIf(userById == null, ErrorCode.NOT_FOUND_ERROR);
    return ResultUtils.success(userById);
  }
  
  /**
   * Get User VO by id.
   *
   * @param id
   * @param request
   * @return
   */
  @GetMapping("/get/vo")
  public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
    BaseResponse<User> response = getUserById(id, request);
    User userVOById = response.getData();
    return ResultUtils.success(userService.getUserVO(userVOById));
  }
  
  /**
   * Get user by page (only for admin).
   *
   * @param userQueryRequest
   * @param httpServletRequest
   * @return
   */
  @PostMapping("/list/page")
  @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
  public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest httpServletRequest) {
    long currentPage = userQueryRequest.getCurrentPage();
    long size = userQueryRequest.getPageSize();
    Page<User> userPage = userService.page(new Page<>(currentPage, size), userService.getQueryWrapper((userQueryRequest)));
    return ResultUtils.success(userPage);
  }
  
  /**
   * Get user VO by page (only for admin).
   *
   * @param userQueryRequest
   * @param request
   * @return
   */
  @PostMapping("/list/page/vo")
  public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                     HttpServletRequest request) {
    if (userQueryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    long current = userQueryRequest.getCurrentPage();
    long size = userQueryRequest.getPageSize();
    
    ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
    Page<User> userPage = userService.page(new Page<>(current, size),
        userService.getQueryWrapper(userQueryRequest));
    Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
    List<UserVO> userVOList = userService.getUserVO(userPage.getRecords());
    userVOPage.setRecords(userVOList);
    return ResultUtils.success(userVOPage);
  }
  
  /**
   * User update info.
   *
   * @param userUpdateMyRequest
   * @param request
   * @return
   */
  @PostMapping("/update/my")
  public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                            HttpServletRequest request) {
    if (userUpdateMyRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User loginUser = userService.getLoginUser(request);
    User user = new User();
    BeanUtils.copyProperties(userUpdateMyRequest, user);
    user.setId(loginUser.getId());
    boolean isUpdate = userService.updateById(user);
    ThrowUtils.throwIf(!isUpdate, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(true);
  }
}
