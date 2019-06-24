package com.mmall.service;

import com.mmall.pojo.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: cls
 **/

public interface IOrderItemService {

    List<OrderItem> selectByOrderNo(long OrderNo);

}
