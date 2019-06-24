package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;


import java.util.Date;
import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(long orderNo);

    Order selectByOrderNoAndUserId(@Param(value="orderNo")long orderNo,@Param(value="userId")int userId);

    List<Order> selectOrderListByUserId(int userId);

    List<Order> selectOrderNeedClose(@Param(value="closeTime")String closeTime, @Param(value="waitPayStatus")int waitPayStatus);

    int cancelOrderById(@Param(value="status")int status,@Param(value="id")int id);

}