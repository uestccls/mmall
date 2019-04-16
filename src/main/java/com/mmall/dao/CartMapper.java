package com.mmall.dao;

import com.mmall.pojo.Cart;
import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param(value = "userId") int userId, @Param(value = "productId")int productId);

    List<Cart> selectByUserId(int userId);

    List<Cart> selectByUserIdAndChecked(int userId);

    int batchDeleteById(@Param("cartIdList") List cartIdList);



}