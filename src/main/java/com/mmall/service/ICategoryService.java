package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: cls
 **/
public interface ICategoryService {

    ServerResponse getCategory(int parentId);
    ServerResponse add_category(Category category);
    ServerResponse changeCategoryName(int id,String name);
    ServerResponse<List<Integer>> getCategoryAndChildById(int parentId);

}
