package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.dateConvertUtil;
import com.mmall.util.ftpPropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: cls
 **/
@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;

    /**
     * @Description:  新增or更新产品
     * @param product
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse addOrUpdateProduct(Product product){
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){  // 如果修改的信息包含了SubImages(图片url)
                String[] subImagesArray=product.getSubImages().split(",");  //与前端规定以 , 作为图片url之间的分割
                product.setMainImage(subImagesArray[0]);  //与前端规定将第一个作为主图
            }
            // 与前端规定：更新产品需要传入productId，新增则不需要
            if(product.getId()!=null){    // 更新产品
                int rowCount=productMapper.updateByPrimaryKeySelective(product);
                if(rowCount>0){
                    return ServerResponse.createSuccessMsg("更新产品成功");
                }else {
                    return ServerResponse.createErrorMessage("数据库修改异常，更新产品失败");
                }
            }else {    // 新增产品
                int rowCount=productMapper.insert(product);
                if(rowCount>0){
                    return ServerResponse.createSuccessMsg("新增产品成功");
                }else {
                    return ServerResponse.createErrorMessage("数据库修改异常，新增产品失败");
                }
            }
        }
        return ServerResponse.createErrorMessage("新增or更新产品的参数错误");
    }

    @Override
    public ServerResponse setSaleStatus(Integer status,Integer productId){
        if(status==null||productId==null){
            return ServerResponse.createErrorCodeMessage(ResponseCode.Illegal_Argument.getCode(),ResponseCode.Illegal_Argument.getInfo());
        }
        int rowCount=productMapper.updateSaleStatus(status,productId);
        if(rowCount>0){
            return ServerResponse.createSuccessMsg("更新产品上下架状态成功");
        }else {
            return ServerResponse.createErrorMessage("参数错误，更新产品上下架状态成功失败");
        }
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createErrorCodeMessage(ResponseCode.Illegal_Argument.getCode(),ResponseCode.Illegal_Argument.getInfo());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        ProductDetailVo productDetailVo=setProductDetailVo(product);

        return ServerResponse.createSuccessMsgData("查询该商品详情成功",productDetailVo);
    }

    public ProductDetailVo setProductDetailVo(Product product){
        ProductDetailVo productDetailVo =new ProductDetailVo();
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        // parentCategoryId、imageHost、createTime、updateTime

        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category.getParentId()==null){
            productDetailVo.setParentCategoryId(0);  // 默认为根节点 (0)
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setImageHost(ftpPropertiesUtil.getValue("ftp.server.http.prefix"));
        String createTime=dateConvertUtil.dateToString(product.getCreateTime(),"yyyy-MM-dd HH:mm:ss"); //转换成string方便前端显示
        productDetailVo.setCreateTime(createTime);
        String updateTime=dateConvertUtil.dateToString(product.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"); //转换成string方便前端显示
        productDetailVo.setUpdateTime(updateTime);

        return  productDetailVo;
    }

    /**
     * @Description:  获取全部商品list
     * @param
     * @return: java.util.List<com.mmall.vo.ProductListVo>
     */
    @Override
    public List<ProductListVo> getProductList(){
        List<Product> productList=productMapper.selectAllProduct();
        List<ProductListVo> productListVoList=new ArrayList<>();
        for(int i=0;i<productList.size();i++){
            Product product=productList.get(i);
            ProductListVo pdlv=setProductListVo(product);
            productListVoList.add(pdlv);

        }
        return productListVoList;
    }

    public ProductListVo setProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(ftpPropertiesUtil.getValue("ftp.server.http.prefix"));

        return productListVo;
    }

    /**
     * @Description:   搜索商品 productName、productId参数可以传入一个
     * @param productName  模糊搜索（like %productName%）
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize){
        if(productId==null&&productName==null){
            return ServerResponse.createErrorCodeMessage(ResponseCode.Illegal_Argument.getCode(),ResponseCode.Illegal_Argument.getInfo());
        }
        if(productName!=null){
            StringBuilder name= new StringBuilder();
            productName=name.append("%").append(productName).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectProductByNameAndId(productName,productId);
        if(productList.size()==0){
            return ServerResponse.createErrorMessage("搜索的商品不存在");
        }
        List<ProductListVo> productListVoList=new ArrayList<>();
        for(int i=0;i<productList.size();i++){
            productListVoList.add(setProductListVo(productList.get(i)));
        }
        PageInfo<ProductListVo> pageInfo = new PageInfo<>(productListVoList);//封装结果集到PageInfo bean
        return ServerResponse.createSuccessMsgData("搜索商品成功",pageInfo);
    }





}
