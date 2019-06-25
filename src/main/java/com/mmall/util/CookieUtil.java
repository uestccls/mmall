package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: cls
 **/
public class CookieUtil {

    private static Logger logger= LoggerFactory.getLogger(CookieUtil.class);

    private final static String COOKIE_DOMAIN=".chenlis.cn";
    private final static String COOKIE_NAME="mmall_login_token";
    private final static String SpringSessionCOOKIE_NAME="SPRIRNG-SESSION-NAME";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                if(COOKIE_NAME.equals(ck.getName())){
                    logger.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static String readSPRIRNGSESSION(HttpServletRequest request){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                if(SpringSessionCOOKIE_NAME.equals(ck.getName())){
                    logger.info("read SpringSessionCOOKIE_NAME:{},cookieValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck=new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");  // 表示设置在根目录
        ck.setHttpOnly(true);

        // 单位 秒
        // 如果maxage不设置的话，cookie就不会写入硬盘，而是写入内存。只在当前页面有效
        ck.setMaxAge(60*60*24*365);  // 如果-1，代表永久
        logger.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);

    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);  //设置成0，代表删出此cookie
                    logger.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }

        }

    }

    public static String getCookieDomain() {
        return COOKIE_DOMAIN;
    }

    public static String getCookieName() {
        return COOKIE_NAME;
    }
}
