package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.vo.PayPreCreateVo;

import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: cls
 **/
public interface IPayInfoService {

    ServerResponse<PayPreCreateVo> pay(int userId,long orderNo,String path) throws IOException;
    ServerResponse queryOrderStatus(long orderNo);
    ServerResponse alipayCallback(Map<String,String> Params);
    ServerResponse queryOrderPay(long orderNo,int userId);

}
