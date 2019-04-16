package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.ShippingAddress;
import com.mmall.service.IShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: cls
 **/
@Service("iShippingAddressService")
public class IShippingAddressServiceImpl implements IShippingAddressService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse newAddress(ShippingAddress shippingAddress){
        int cow=shippingMapper.insert(shippingAddress);
        if(cow>0){
            return ServerResponse.createSuccessMsg("新增地址成功");
        }
        return ServerResponse.createErrorMessage("数据库插入异常，新增失败");
    }



}
