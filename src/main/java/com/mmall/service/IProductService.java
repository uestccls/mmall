package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: cls
 **/
public interface IProductService {

    ServerResponse addOrUpdateProduct(Product product);
    ServerResponse setSaleStatus(Integer status,Integer productId);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    List<ProductListVo> getProductList();
    ServerResponse searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);

}
