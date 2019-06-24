package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
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
import java.io.Serializable;

/**
 * @description:  购物车
 * @author: cls
 **/

@Controller
@RequestMapping("/cart")
public class cartController {

    @Autowired
    IUserService iUserService;
    @Autowired
    ICartService iCartService;

    /**
     * @Description:  加入购物车
     * @param session
     * @param productId
     * @param quantity
     * @return: com.mmall.common.ServerResponse  返回购物车List
     */
    @RequestMapping(value = "/addToCart")
    @ResponseBody
    ServerResponse addToCart(HttpSession session, HttpServletRequest httpServletRequest,int productId, int quantity){
//        User user=(User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iCartService.addToCart(user.getId(),productId,quantity);
        }
        return response;
    }

    /**
     * @Description:  购物车中选择商品进行结算 （一个一个选的 全选功能就直接给以一个allChecked=true标志,然后再数据库中将所有该用户的商品cheked置1 比较简单，就没做了）
     * @param session   （购物车初始状态下，所有商品应该是 没有checked的状态）
     * @param productId
     * @return: com.mmall.common.ServerResponse  返回购物车所有List，但只计算勾选了的商品的总价
     */
    @RequestMapping(value = "/chooseCart")
    @ResponseBody
    ServerResponse chooseCart(HttpSession session, HttpServletRequest httpServletRequest,int productId){
//        User user=(User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iCartService.chooseCart(user.getId(),productId);
        }
        return response;
    }



}
