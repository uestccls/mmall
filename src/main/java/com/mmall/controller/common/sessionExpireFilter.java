package com.mmall.controller.common;


import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @description:   拦截所有请求，重置session时间
 * @author: cls
 **/
public class sessionExpireFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(loginToken!=null){
            User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken),User.class);
            if(user!=null){
                RedisShardedPoolUtil.expire(loginToken, Const.redis.redisSessionTime);  // 重置redis中cookie有效时间
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

}
