package com.mmall.service.impl;

import com.mmall.dao.OrderItemMapper;
import com.mmall.pojo.OrderItem;
import com.mmall.service.IOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: cls
 **/
@Service("OrderItemService")
public class IOrderItemServiceImpl implements IOrderItemService {

    @Autowired
    OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> selectByOrderNo(long OrderNo) {
        List<OrderItem> list=orderItemMapper.selectByOrderNo(OrderNo);

        return list;
    }


}
