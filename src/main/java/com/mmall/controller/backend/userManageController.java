package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.controller.portal.userController;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @description:  后台 管理员类
 * @author: cls
 **/
@Controller
@RequestMapping("/manage/user")
public class userManageController {

    @Autowired
    IUserService iUserService;

    /**
     * @Description:  管理员登录
     * @param username
     * @param password
     * @param session
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
     */
    @RequestMapping(value ="/managerLogin",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> managerLogin(String username, String password, HttpSession session){
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            int role=response.getData().getRole();
            if(role== Const.Role_Admin){   // 是管理员
                session.setAttribute(Const.Current_User,response.getData());
                return ServerResponse.createSuccessMsgData("管理员登录成功",response.getData());
            }else {
                return ServerResponse.createErrorMessage("用户密码正确，但是该用户不是管理员，无法进行管理员登录");
            }
        }
        return response;
    }

}
