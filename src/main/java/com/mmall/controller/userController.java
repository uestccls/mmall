package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;

@Controller()
@RequestMapping("/user")
public class userController {

    @Autowired
    private IUserService iUserService;

    /**
     * @Description: 用户登录
     * @param username
     * @param password
     * @return: java.lang.String
     * @Author: cls
     * @Date: 2019/2/22
     */
    @RequestMapping(value ="/login",method = RequestMethod.POST)
    @ResponseBody  // 将返回值通过springmvc的jason插件序列化成jason
    public ServerResponse<User> login(String username, String password, HttpSession session){
       ServerResponse<User> response=iUserService.login(username,password);
       if(response.isSuccess()){    // 登录成功则将User注入session
           session.setAttribute(Const.Current_User,response.getData());
       }
       System.out.println(response);
       return response;
    }


}
