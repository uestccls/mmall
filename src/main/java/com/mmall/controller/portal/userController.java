package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @ResponseBody  // 将返回值通过springmvc的jason插件 序列化 成jason
    /* 在使用 @RequestMapping后，返回值通常解析为跳转路径，
       但是加上 @ResponseBody 后返回结果不会被解析为跳转路径，
       通过适当的转换器转换为指定的格式之后(配置的 MappingJackson2HttpMessageConverter)
       直接写入 HTTP response body 中
    */
    public ServerResponse<User> login(String username, String password, HttpSession session,HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
       ServerResponse<User> response=iUserService.login(username,password);
       if(response.isSuccess()){    // 登录成功则将User注入session
//           session.setAttribute(Const.Current_User,response.getData());

           CookieUtil.writeLoginToken(httpServletResponse,session.getId());
           RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.objToString(response.getData()),Const.redis.redisSessionTime);  // 将sessionId-user存入redis

       }
//       System.out.println(response);
       return response;
    }
    /**
     * @Description: 登出
     * @param session
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value ="/logout",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse logout(HttpSession session,HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){

//        session.removeAttribute(Const.Current_User);  // 清除 当前 session
//        session.invalidate();
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);  // 直接删出cookie中的token
        ServerResponse<User> response= ServerResponse.createSuccessMsg("注销成功！");
        return response;
    }

    /**
     * @Description: 注册新用户
     * @param user
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
     */
    @RequestMapping(value="/register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> register(User user){
        ServerResponse response=iUserService.register(user);
        return response;
    }

    /**
     * @Description: 检测注册的用户名是否存在
     * @param type
     * @param str
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value="/checkValid",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String type,String str){
        ServerResponse response=iUserService.checkValid(type,str);
        return response;
    }

    /**
     * @Description: 登录状态下获取该用户信息
     * @param
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
     */
    @RequestMapping(value="/getUserInfo",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session,HttpServletRequest httpServletRequest){
        String token=CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user=JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
//        User user= (User) session.getAttribute(Const.Current_User);
        if(user!=null){
//            System.out.println(user);
            return ServerResponse.createSuccessMsgData("查询成功",user);
        }else {
            return ServerResponse.createErrorMessage("用户未登录");
        }
    }

    /**
     * @Description:  忘记密码后 返回提示问题、答案
     * @param
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
     */
    @RequestMapping(value="/forgetPassword",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPassword(String username){
        ServerResponse response=iUserService.forgetPassword(username);
        return response;

    }

    /**
     * @Description: 忘记密码后 校验答案 正确后返回服务器一个 token
     * @param username
     * @param question
     * @param answer
     * @return: com.mmall.common.ServerResponse<java.lang.String>
     */
    @RequestMapping(value="/forgetCheckAnswer",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){

        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * @Description:   忘记密码，回答问题正确则重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value="/forgetResetPassword",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * @Description:  登陆状态下，修改密码
     * @param passwordOld
     * @param passwordNew
     * @param session
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value="/resetPassword",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(String passwordOld,String passwordNew,HttpSession session){
        User user= (User) session.getAttribute(Const.Current_User);
        if(user==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * @Description: 更新用户信息
     * @param user
     * @param session
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
     */
    @RequestMapping(value="/updateUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(User user,HttpSession session,HttpServletRequest httpServletRequest){
        String token=CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User currentUser=JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        if(currentUser==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());  // 保证id不变
        ServerResponse response=iUserService.updateUserInfo(user);
        if(response.isSuccess()){   // 修改成功，则更新session
//            session.setAttribute(Const.Current_User,response.getData());
            RedisShardedPoolUtil.setEx(token,JsonUtil.objToString(response.getData()),Const.redis.redisSessionTime); // 更新redis，数据一致性
        }
        return response;
    }


}
