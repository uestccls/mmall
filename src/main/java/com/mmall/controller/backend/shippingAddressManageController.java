package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.ShippingAddress;
import com.mmall.pojo.User;
import com.mmall.service.IShippingAddressService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @description:  收货地址
 * @author: cls
 **/
@Controller
@RequestMapping("/shippingAddress")
public class shippingAddressManageController {

    @Autowired
    IUserService iUserService;
    @Autowired
    IShippingAddressService iShippingAddressService;

    /**
     * @Description:  新建收货地址
     * @param session
     * @param shippingAddress
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/newAddress")
    @ResponseBody
    ServerResponse newAddress(HttpSession session, HttpServletRequest httpServletRequest,ShippingAddress shippingAddress){
//        User user=(User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            shippingAddress.setUserId(user.getId());
            return iShippingAddressService.newAddress(shippingAddress);
        }
        return response;

    }


}
