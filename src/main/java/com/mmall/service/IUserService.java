package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @description:
 * @author: cls
 * @create: 2019-02-22 10:51
 **/
public interface IUserService {

     ServerResponse<User> login(String username, String password);

}
