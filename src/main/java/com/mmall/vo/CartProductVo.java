package com.mmall.vo;

import java.math.BigDecimal;

/**
 * @description:  购物车中需要的信息类
 * @author: cls
 **/
public class CartProductVo {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Integer checked;

    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private String productDetail;
    private BigDecimal productPrice;
    private Integer productStock;
    private Integer productStatus;
    private BigDecimal productTotalPrice;  // price*quantity 这一个商品的总价

    private String limitQuantity;  // 判断商品数量是否大于库存 失败的：LIMIT_NUM_FAIL 成功的：LIMIT_NUM_SUCCESS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    public String getProductMainImage() {
        return productMainImage;
    }

    public void setProductMainImage(String productMainImage) {
        this.productMainImage = productMainImage;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }

    @Override
    public String toString() {
        return "CartProductVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", checked=" + checked +
                ", productName='" + productName + '\'' +
                ", productSubtitle='" + productSubtitle + '\'' +
                ", productMainImage='" + productMainImage + '\'' +
                ", productDetail='" + productDetail + '\'' +
                ", productPrice=" + productPrice +
                ", productStock=" + productStock +
                ", productStatus=" + productStatus +
                ", productTotalPrice=" + productTotalPrice +
                ", limitQuantity='" + limitQuantity + '\'' +
                '}';
    }
}
