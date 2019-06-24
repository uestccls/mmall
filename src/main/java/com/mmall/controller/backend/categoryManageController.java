package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @description: 后台 分类  都需要管理员权限操作
 * @author: cls
 **/
@Controller
@RequestMapping("/manage/category")
public class categoryManageController {

    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IUserService iUserService;

    /**
     * @Description: 根据parentId查询 所有Category信息 需要管理员权限
     * @param parentId  （默认值为 0 ）
     * @param session
     * @return: com.mmall.common.ServerResponse<com.mmall.pojo.Category>
     */
    @RequestMapping(value = "/getCategory",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Category> getCategory(@RequestParam(value = "categoryId",defaultValue = "0") int parentId,HttpSession session,HttpServletRequest httpServletRequest){
//        User user=(User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iCategoryService.getCategory(parentId);
        }
        return response;
    }

    /**
     * @Description: 添加品类
     * @param parentId (default=0)
     * @param status (default=0)
     * @param name
     * @param session
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/addCategory",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(defaultValue = "0")int parentId,@RequestParam(defaultValue = "1")int status,@RequestParam(value = "categoryName")String name,HttpSession session,HttpServletRequest httpServletRequest){
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            Category category=new Category();
            category.setParentId(parentId);
            category.setStatus(status);
            category.setName(name);
            return iCategoryService.add_category(category);
        }
        return response;

    }

    /**
     * @Description: 通过id 更新品类名字成功
     * @param id
     * @param categoryName
     * @param session
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/changeCategoryName",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse changeCategoryName(@RequestParam(value = "categoryId") int id,String categoryName,HttpSession session,HttpServletRequest httpServletRequest){
//        User user=(User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iCategoryService.changeCategoryName(id,categoryName);
        }
        return response;
    }

    /**
     * @Description:  查询当前分类id及递归子节点id
     * @param parentId
     * @param session
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/getCategoryAndChildId",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndChildId(@RequestParam(defaultValue = "0")int parentId, HttpSession session,HttpServletRequest httpServletRequest){
//        User user=(User) session.getAttribute(Const.Current_User);
        String token= CookieUtil.readLoginToken(httpServletRequest);
        if(token==null){
            return ServerResponse.createErrorMessage("用户未登录");
        }
        User user= JsonUtil.stringToObj(RedisShardedPoolUtil.get(token),User.class); // 改为通过session从redis中查询 User
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iCategoryService.getCategoryAndChildById(parentId);
        }
        return response;
    }


}
