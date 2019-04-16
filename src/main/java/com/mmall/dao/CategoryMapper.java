package com.mmall.dao;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectByParentId(int parentId);

    int changeCategoryNameById(@Param("id") int id, @Param("name") String name);

    List<Integer> getDeepCategory(int parentId);

}