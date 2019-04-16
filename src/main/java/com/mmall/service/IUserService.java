package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: cls
 * @create: 2019-02-22 10:51
 **/
public interface IUserService {

     ServerResponse<User> login(String username, String password);
     ServerResponse register(User user);
     ServerResponse checkValid(String type,String str);
     ServerResponse<String> forgetPassword(String username);
     ServerResponse<String> checkAnswer(String username,String question,String answer);
     ServerResponse forgetResetPassword(String username,String passwordNew,String forgetToken);
     ServerResponse resetPassword(String passwordOld,String passwordNew,User user);
     ServerResponse updateUserInfo(User user);

     ServerResponse checkAdminRole(User user);
}
