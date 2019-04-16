package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.pojo.User;
import com.mmall.service.IPayInfoService;
import com.mmall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


/**
 * @description:  支付
 * @author: cls
 **/
@Controller
@RequestMapping(value = "/payInfo")
public class payInfoController {

    private static final Logger logger= LoggerFactory.getLogger(payInfoController.class);

    @Autowired
    IUserService iUserService;
    @Autowired
    IPayInfoService iPayInfoService;

    /**
     * @Description:  生成支付二维码
     * @param session
     * @param orderNo 商户网站订单系统中唯一 订单号
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/pay")
    @ResponseBody
    ServerResponse pay(HttpSession session,HttpServletRequest request,long orderNo) throws IOException {
        User user=(User) session.getAttribute(Const.Current_User);
        String realPath = request.getSession().getServletContext().getRealPath("/");  //获取项目绝对路径: C:\SOFT\Work\apache-tomcat-8.0.53\webapps\ROOT\
//        System.out.println(realPath);                                                    //作为二维码存放的位置
        if(user!=null){   // 已经登录

            return iPayInfoService.pay(user.getId(),orderNo,realPath);
        }
        return ServerResponse.createErrorCodeMessage(ResponseCode.Need_Login.getCode(),ResponseCode.Need_Login.getInfo());
    }


    /**
     * @Description:   查询订单状态 (应该在指定时间内轮询，3~5s查询一次)
     * @param session
     * @param orderNo
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/queryOrderStatus")
    @ResponseBody
    ServerResponse queryOrderStatus(HttpSession session,long orderNo){
        User user=(User) session.getAttribute(Const.Current_User);
        if(user!=null){   // 已经登录

            return iPayInfoService.queryOrderStatus(orderNo);
        }
        return ServerResponse.createErrorCodeMessage(ResponseCode.Need_Login.getCode(),ResponseCode.Need_Login.getInfo());
    }

    /**
     * @Description:  处理支付宝回调
     * @param request
     * @return: java.lang.Object
     */
    @RequestMapping(value = "/alipayCallback")
    @ResponseBody
    Object alipayCallback(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();  // HttpServletRequest中得到的map 只能度（原因在下面），把它转换成 Map<String, String>才能操作
        Map<String, String> myParams = new HashMap<>();           // 请求参数中可能有多个key相同，就把它们的值放入同一个String[]
//        params.remove("sign_type");   // ！！！！！不能再这里就移除，否则程序就会卡死在这一步！！！ 至于原因？
                                    // 因为HttpServletRequest中的map继承了一个Collections.unmodifiableMap()方法，使得集合为只读！所以不能remove
        for (String key : params.keySet()) {
            String name = (String) key;
            String[] value = (String[]) params.get(key);
            String valueStr = "";
            for (int i = 0; i < value.length; i++) {
                valueStr = (i == value.length - 1) ? valueStr + value[i] : valueStr + value[i] + ","; //多个字符串之间 , 分割
            }
            myParams.put(name, valueStr);
        }
//        System.out.println("okokokokok3!!!!!!~~~~~~~~~");
        logger.info("支付宝回调,sign:{},trade_Status:{},参数:{}", myParams.get("sign"), myParams.get("trade_Status"), myParams.toString());
//        System.out.println("okokokokok4!!!!!!~~~~~~~~~");
        try {
            myParams.remove("sign_type");
//            System.out.println("okokokokok5!!!!!!~~~~~~~~~");
            // 调用支付宝验签函数
            boolean AlipayrsaCheckV2Result = AlipaySignature.rsaCheckV2(myParams, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!AlipayrsaCheckV2Result) {
                System.out.println("支付宝回调验签不正确");
                return ServerResponse.createErrorMessage("支付宝回调验签不正确");
            }
        } catch (AlipayApiException e) {
            logger.error("回调(支付宝)异常", e);
        }

        // 本地验证数据正确性
        ServerResponse serverResponse = iPayInfoService.alipayCallback(myParams);
        if (serverResponse.isSuccess()) {
            return Const.alipayCallBack.responseSuccess;  // 返回给支付宝字符串： success 让支付宝停止发送这次交易状态的通知（交易状态改变，它又会发送通知）
        } else {
            return Const.alipayCallBack.responseFailed;  // 验证错误就随便返回什么了，支付宝会重发异步通知
        }
    }

    /**
     * @Description:  查询订单是否付款 (与queryOrderStatus不同的是：该方法间接通过支付宝回调信息查看订单是否付款)
     * @param
     * @return: java.lang.Object
     */
    @RequestMapping(value = "/queryOrderPay")
    @ResponseBody
    ServerResponse queryOrderPay(HttpSession session,long orderNo){
        User user=(User) session.getAttribute(Const.Current_User);
        if(user!=null){   // 已经登录
            return iPayInfoService.queryOrderPay(orderNo,user.getId());
        }
        return ServerResponse.createErrorCodeMessage(ResponseCode.Need_Login.getCode(),ResponseCode.Need_Login.getInfo());
    }



}
