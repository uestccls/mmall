package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.ShippingAddress;

/**
 * @description:
 * @author: cls
 **/
public interface IShippingAddressService {

    ServerResponse newAddress(ShippingAddress shippingAddress);

}
