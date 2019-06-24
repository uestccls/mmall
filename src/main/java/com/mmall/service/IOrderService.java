package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: cls
 **/
public interface IOrderService {

    ServerResponse createOrder(int userId,int shippingAddressId);
    ServerResponse getOderList(int userId);
    List selectOrderNeedClose(String closeTime);
    void closeOrder();


}
