package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * @description:
 * @author: cls
 **/
public interface IOrderService {

    ServerResponse createOrder(int userId,int shippingAddressId);
    ServerResponse getOderList(int userId);


}
