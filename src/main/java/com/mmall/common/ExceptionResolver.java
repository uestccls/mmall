package com.mmall.common;

import com.mmall.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:   处理全局异常
 * @author: cls
 **/
@Component
public class ExceptionResolver implements HandlerExceptionResolver{
    private static Logger logger= LoggerFactory.getLogger(ExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        logger.error("{} Exception",httpServletRequest.getRequestURI(),e);
        ModelAndView modelAndView=new ModelAndView(new MappingJackson2JsonView());

        modelAndView.addObject("status",ResponseCode.ERROR);
        modelAndView.addObject("msg","接口异常，详情请查看服务端日志");
        modelAndView.addObject("data",e.toString());
        return modelAndView;
    }

}
