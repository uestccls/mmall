package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:  购物车信息返回类
 * @author: cls
 **/
public class CartVo {
    private List<CartProductVo> cartProductVoList;
    boolean allChecked;  // 是否全选
    private BigDecimal cartTotalPrice;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    @Override
    public String toString() {
        return "CartVo{" +
                "cartProductVoList=" + cartProductVoList +
                ", allChecked=" + allChecked +
                ", cartTotalPrice=" + cartTotalPrice +
                '}';
    }

}
