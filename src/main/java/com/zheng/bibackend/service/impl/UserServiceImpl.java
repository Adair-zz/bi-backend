package com.zheng.bibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.constant.CommonConstant;
import com.zheng.bibackend.constant.UserConstant;
import com.zheng.bibackend.exception.BusinessException;
import com.zheng.bibackend.mapper.UserMapper;
import com.zheng.bibackend.model.dto.user.UserQueryRequest;
import com.zheng.bibackend.model.entity.User;
import com.zheng.bibackend.model.enums.UserRoleEnum;
import com.zheng.bibackend.model.vo.LoginUserVO;
import com.zheng.bibackend.model.vo.UserVO;
import com.zheng.bibackend.service.UserService;
import com.zheng.bibackend.utils.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User service implements.
 *
* @author Zheng Zhang
* @description 针对表【user(user)】的数据库操作Service实现
* @createDate 2023-04-22 17:21:28
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
  
  @Resource
  private UserMapper userMapper;
  
  /**
   * Salt Encryption
   */
  private static final String SALT = "zheng";
  
  @Override
  public long userRegister(String userAccount, String userPassword, String confirmPassword) {
    if (StringUtils.isAnyBlank(userAccount, userPassword, confirmPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Empty params");
    }
    if (userAccount.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "The length of user account is less than 4");
    }
    if (userPassword.length() < 8 || confirmPassword.length() < 8) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password is short");
    }
    if (!userPassword.equals(confirmPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Confirm password is wrong");
    }
    // do not allow special letters in user account
    // todo
    synchronized (userAccount.intern()) {
      // do not allow duplicate account
      // not allow duplicate user account
      QueryWrapper<User> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("userAccount", userAccount);
      long count = this.baseMapper.selectCount(queryWrapper);
      if (count > 0) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "Duplicate user account");
      }
      
      // password encrypt
      String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
      // save user
      User newUser = new User();
      newUser.setUserAccount(userAccount);
      newUser.setUserPassword(encryptedPassword);
      boolean isSaveNewUser = this.save(newUser);
      if (!isSaveNewUser) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Register failed, database error");
      }
      return newUser.getId();
    }
  }
  
  @Override
  public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
    if (StringUtils.isAnyBlank(userAccount, userPassword)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Empty Params");
    }
    if (userAccount.length() < 4) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Wrong User Account");
    }
    if (userPassword.length() < 8) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Wrong Password");
    }
  
    // password encrypt
    String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
    // check if user exists
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userAccount", userAccount);
    queryWrapper.eq("userPassword", encryptedPassword);
    User user = this.baseMapper.selectOne(queryWrapper);
    // user doesn't exist
    if (user == null) {
      log.info("User Login failed. Account or password is wrong");
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "User doesn't exist or password is wrong");
    }
    // save user login status
    httpServletRequest.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
    
    return this.getLoginUserVO(user);
  }
  
  @Override
  public User getLoginUser(HttpServletRequest httpServletRequest) {
    // check if user login
    User currentLoginUser = (User) httpServletRequest.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    if (currentLoginUser == null || currentLoginUser.getId() == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    // get user info from database if user exists
    long userId = currentLoginUser.getId();
    currentLoginUser = this.getById(userId);
    if (currentLoginUser == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    return currentLoginUser;
  }
  
  @Override
  public boolean isAdmin(HttpServletRequest httpServletRequest) {
    User user = (User) httpServletRequest.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    return isAdmin(user);
  }
  
  @Override
  public boolean isAdmin(User user) {
    return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
  }
  
  @Override
  public boolean userLogout(HttpServletRequest httpServletRequest) {
    if (httpServletRequest.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, "Not Login");
    }
    // remove login state
    httpServletRequest.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    return true;
  }
  
  @Override
  public LoginUserVO getLoginUserVO(User user) {
    if (user == null) {
      return null;
    }
    LoginUserVO loginUserVO = new LoginUserVO();
    BeanUtils.copyProperties(user, loginUserVO);
    return loginUserVO;
  }
  
  @Override
  public UserVO getUserVO(User user) {
    if (user == null) {
      return null;
    }
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    return userVO;
  }
  
  @Override
  public List<UserVO> getUserVO(List<User> userList) {
    if (CollectionUtils.isEmpty(userList)) {
      return new ArrayList<>();
    }
    return userList.stream().map(this::getUserVO).collect(Collectors.toList());
  }
  
  @Override
  public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
    if (userQueryRequest == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "Empty params");
    }
    
    Long id = userQueryRequest.getId();
    String userName = userQueryRequest.getUserName();
    String userRole = userQueryRequest.getUserRole();
    String sortField = userQueryRequest.getSortField();
    String sortOrder = userQueryRequest.getSortOrder();
    
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq(id != null, "id", id);
    queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
    queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
    queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.ASCENDING_ORDER),
        sortField);
    return queryWrapper;
  }
}




