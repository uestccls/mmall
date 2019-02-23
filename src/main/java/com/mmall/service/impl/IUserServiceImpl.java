package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: cls
 * @create: 2019-02-22 10:53
 **/
@Service("iUserService")
public class IUserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        User user=userMapper.selectBaseUserByUsername(username);
        if(user==null){
            return ServerResponse.CreateErrorMessage("用户名不存在");
        }
        if(!user.getPassword().equals(password)){
            return ServerResponse.CreateErrorMessage("密码错误");
        }
//        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createSuccessMsgData("登录成功",user);
    }


}
