package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: cls
 **/
@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService {

    @Autowired
    IUserService iUserService;
    @Autowired
    CategoryMapper categoryMapper;

    /**
     * @Description: 根据parentId查询 所有Category信息
     * @param parentId
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.Category>
     */
    @Override
    public ServerResponse<List<Category>> getCategory(int parentId) {
        List<Category> categoryList=categoryMapper.selectByParentId(parentId);
        if(categoryList.size()==0){
            return ServerResponse.createErrorMessage("该id 没有信息");
        }
        return ServerResponse.createSuccessMsgData("查询成功",categoryList);
    }

    /**
     * @Description: 添加品类
     * @param category
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse add_category(Category category){
        int resultCow=categoryMapper.insert(category);
        if(resultCow>0){
            return ServerResponse.createSuccessMsg("添加品类成功");
        }
        return ServerResponse.createErrorMessage("数据库插入异常，添加失败");
    }

    /**
     * @Description: 通过id 更新品类名字成功
     * @param id
     * @param name
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse changeCategoryName(int id,String name){
        int resultCow=categoryMapper.changeCategoryNameById(id,name);
        if(resultCow>0){
            return ServerResponse.createSuccessMsg("更新品类名字成功");
        }
        return ServerResponse.createErrorMessage("数据库插入异常，修改失败");

    }

    /**
     * @Description:  查询当前分类id及递归子节点id
     * @param parentId
     * @return: com.mmall.common.ServerResponse<java.util.List<java.lang.Integer>>
     */
    @Override
    public ServerResponse<List<Integer>> getCategoryAndChildById(int parentId){
        List<Integer> categoryIdList=new ArrayList<>();
        Set<Category> categorySet=new HashSet<>();
        findChildCategory(categorySet,parentId);
        if(categorySet.size()>0){
            for(Category category:categorySet){
                categoryIdList.add(category.getId());
            }
            return ServerResponse.createSuccessMsgData("查询成功",categoryIdList);
        }
        return ServerResponse.createErrorMessage("该id下的 数据为空");
    }

    // 通过parentId递归查找本身及子分类
    private Set<Category> findChildCategory(Set<Category> categorySet,int parentId){

        // 递归，直到categoryList为空
        List<Category> categoryList=categoryMapper.selectByParentId(parentId);
        for(Category categoryS: categoryList){
            categorySet.add(categoryS);
            findChildCategory(categorySet,categoryS.getId()); // 将子类（id）当作parentId递归查询
        }
        return categorySet;
    }


}
