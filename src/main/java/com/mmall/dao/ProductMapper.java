package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int updateSaleStatus(@Param(value ="status") Integer status, @Param(value ="id") Integer id);

    List<Product> selectAllProduct();

    List<Product> selectProductByNameAndId(@Param(value = "productName") String productName,@Param(value = "productId")Integer productId);
}