package com.mmall.controller.backend;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mchange.v2.util.PropertiesUtils;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileFTPService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.FtpUtil;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * @description:  商品管理  （管理员操作）
 * @author: cls
 **/
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IFileFTPService iFileFTPService;

    /**
     * @Description: 上传图片到ftp服务器
     * @param session
     * @param file
     * @return: com.mmall.common.ServerResponse<java.util.Map>
     *     返回文件访问的url（访问ftp服务器文件的ip，通过nginx转发 指向ftp服务器的存储根目录）
     */
    @RequestMapping(value = "/uploadPicture",method = RequestMethod.POST)
    @ResponseBody
    ServerResponse<Map> uploadFile(HttpSession session, MultipartFile file) throws IOException {
        User user=(User) session.getAttribute(Const.Current_User);
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iFileFTPService.uploadFile(file);
        }

        return response;
    }

    /**
     * @Description:  新增or更新产品
     * @param session
     * @param product
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/saveProduct")
    @ResponseBody
    ServerResponse saveProduct(HttpSession session, Product product){
        User user=(User) session.getAttribute(Const.Current_User);
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iProductService.addOrUpdateProduct(product);
        }

        return response;
    }

    /**
     * @Description: 产品上下架
     * @param session
     * @param status  商品状态. 1-在售 2-下架 3-删除
     * @param productId  要修改的商品id
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/setSaleStatus")
    @ResponseBody
    ServerResponse setSaleStatus(HttpSession session, Integer status,Integer productId){
        User user=(User) session.getAttribute(Const.Current_User);
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iProductService.setSaleStatus(status,productId);
        }
        return response;
    }

    /**
     * @Description:  后台获取商品详细信息
     * @param session
     * @param productId
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/getProductDetail")
    @ResponseBody
    ServerResponse getProductDetail(HttpSession session,Integer productId){
        User user=(User) session.getAttribute(Const.Current_User);
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iProductService.getProductDetail(productId);
        }
        return response;
    }

    /**
     * @Description:  获取全部商品list 并通过pageHelper分页返回给前端渲染
     * @param session
     * @param pageNum   查询第几页
     * @param pageSize  每页的数量
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/getProductList")
    @ResponseBody
    ServerResponse getProductList(HttpSession session,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "5") Integer pageSize){
        User user=(User) session.getAttribute(Const.Current_User);
        ServerResponse response=iUserService.checkAdminRole(user);
//        System.out.println(pageNum+"----------------------"+pageSize);
        if(response.isSuccess()){   // 是管理员
            PageHelper.startPage(pageNum,pageSize);
            List<ProductListVo> productListVo=iProductService.getProductList();
            PageInfo<ProductListVo> pageInfo = new PageInfo<ProductListVo>(productListVo);//封装结果集到PageInfo bean
            return ServerResponse.createSuccessMsgData("查询所有商品List成功",pageInfo);
        }
        return response;
    }


    /**
     * @Description:  搜索商品 productName、productId参数可以传入一个
     * @param session
     * @param productName   模糊搜索（like %productName%）
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return: com.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/searchProduct")
    @ResponseBody
    ServerResponse searchProduct(HttpSession session,String productName,Integer productId,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "5") Integer pageSize){
        User user=(User) session.getAttribute(Const.Current_User);
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){   // 是管理员
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
        return response;
    }


}
