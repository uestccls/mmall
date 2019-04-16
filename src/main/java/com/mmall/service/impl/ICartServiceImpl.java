package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: cls
 **/
@Service("iCartService")
public class ICartServiceImpl implements ICartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    /**
     * @Description:  加入购物车
     * @param userId
     * @param productId
     * @param quantity
     * @return: com.mmall.common.ServerResponse  返回购物车List
     */
    public ServerResponse addToCart(int userId,int productId, int quantity){
        if(productId==0||quantity==0){
            return ServerResponse.createErrorCodeMessage(ResponseCode.Illegal_Argument.getCode(),"商品id或数量不能为空");
        }
        Cart cart=cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart!=null){  // 说明该用户已经添加了该商品，只需将数量加上即可 (数量不能超过库存)
            int count=cart.getQuantity()+quantity;
//            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
//            if(product.getStock())
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }else {  // 新建一个订单
            Cart cart1=new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(quantity);
            cart1.setChecked(0);  // 加入购物车时认为 没有勾选
            cartMapper.insert(cart1);
        }
        CartVo cartVo=setCartVo(userId,false);  // 默认false
        return ServerResponse.createSuccessMsgData("加入购物车成功，返回购物车List",cartVo);
    }

    public CartVo setCartVo(int userId,Boolean allChecked){
        CartVo cartVo=new CartVo();
        List<Cart> cartList=cartMapper.selectByUserId(userId);  // 查询该用户的所有订单
        List<CartProductVo> cartProductVoList=new ArrayList<>();
        BigDecimal cartTotalPrice= BigDecimal.valueOf(0);
        for(Cart cart : cartList){
            CartProductVo cartProductVo=new CartProductVo();
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(cart.getUserId());
            cartProductVo.setProductId(cart.getProductId());
            cartProductVo.setChecked(cart.getChecked());

            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            cartProductVo.setProductName(product.getName());
            cartProductVo.setProductSubtitle(product.getSubtitle());
            cartProductVo.setProductMainImage(product.getMainImage());
            cartProductVo.setProductDetail(product.getDetail());
            cartProductVo.setProductPrice(product.getPrice());
            cartProductVo.setProductStock(product.getStock());
            cartProductVo.setProductStatus(product.getStatus());
            BigDecimal productTotalPrice=new BigDecimal(0);

            if(cart.getQuantity()<=product.getStock()){  // 小于库存
                cartProductVo.setQuantity(cart.getQuantity());
                cartProductVo.setLimitQuantity("LIMIT_NUM_SUCCESS");
            }else {
                cartProductVo.setQuantity(product.getStock());  // 大于库存，则设置为库存大小
                cartProductVo.setLimitQuantity("LIMIT_NUM_FAIL");
            }

            if(cart.getChecked()==1){  // 选中了该商品才 计算价格
                productTotalPrice= BigDecimalUtil.mul(cartProductVo.getQuantity(),product.getPrice().doubleValue()); // 计算该商品总价
            }
            cartProductVo.setProductTotalPrice(productTotalPrice);

            cartProductVoList.add(cartProductVo);

            cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());  // 累加所有商品总价
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(allChecked);
        cartVo.setCartTotalPrice(cartTotalPrice);
        return cartVo;
    }

    /**
     * @Description:  购物车中选择商品进行结算 （一个一个选的 全选功能就直接给以一个allChecked=true标志,然后再数据库中将所有该用户的商品cheked置1 比较简单，就没做了）
     *                   （购物车初始状态下，所有商品应该是 没有checked的状态）
     * @param productId
     * @return: com.mmall.common.ServerResponse  返回购物车所有List，但只计算勾选了的商品的总价
     */
    public ServerResponse chooseCart(int userId,int productId){
        Cart cart=cartMapper.selectByUserIdAndProductId(userId,productId); // 找到该商品
        if(cart==null){
            return ServerResponse.createSuccessMsg("该用户的订单中没有该商品,无法选择");
        }
        cart.setChecked(Const.cartProductChecked);  // 选中该商品
        cartMapper.updateByPrimaryKeySelective(cart);

        CartVo cartVo=setCartVo(userId,false);  // 默认false，除非有参数让全选
        return ServerResponse.createSuccessMsgData("选中商品成功",cartVo);
    }



}
