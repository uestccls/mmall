package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
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
 * @description:  订单 （前台）
 * @author: cls
 **/
@Controller
@RequestMapping("/order")
public class OrderController {


    @Autowired
    IOrderService iOrderService;


    /**
     * @param session
     * @param shippingAddressId
     * @Description: 根据购物车勾选的商品，生成订单
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/createOrder")
    @ResponseBody
    ServerResponse createOrder(HttpSession session, HttpServletRequest httpServletRequest, int shippingAddressId) {
//        User user = (User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        if (user == null) {
            return ServerResponse.createErrorCodeMessage(ResponseCode.Need_Login.getCode(), ResponseCode.Need_Login.getInfo());
        }
        // 已登录
        return iOrderService.createOrder(user.getId(), shippingAddressId);
    }


    /**
     * @Description:  查询用户的所有订单
     * @param session
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/getOderList")
    @ResponseBody
    ServerResponse getOderList(HttpSession session,HttpServletRequest httpServletRequest) {
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        if (user == null) {
            return ServerResponse.createErrorCodeMessage(ResponseCode.Need_Login.getCode(), ResponseCode.Need_Login.getInfo());
        }
        // 已登录

        return iOrderService.getOderList(user.getId());
    }



}
