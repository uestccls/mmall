package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderItemService;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.dateConvertUtil;
import com.mmall.util.ftpPropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingAddressVo;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * @description:
 * @author: cls
 **/
@Service("iOrderService")
public class IOrderServiceImpl implements IOrderService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ShippingMapper shippingMapper;
    @Autowired
    IOrderItemService iOrderItemService;


    /**
     * @Description:  根据购物车勾选的商品，生成订单
     * @param userId
     * @param shippingAddressId
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse createOrder(int userId,int shippingAddressId){
        Order order=new Order();
        List<Cart> cartList=cartMapper.selectByUserIdAndChecked(userId);  // 查询购物车中用户选中的商品
        if(cartList==null){
            return ServerResponse.createErrorMessage("该用户还没有选择任何商品，无法下单");
        }
        // 生成订单号
        long orderNo=generateOrderNo();
        // 记录订单明细
        ServerResponse OrderItemListServerResponse=setOrderItemList(cartList,orderNo,userId);
        if(!OrderItemListServerResponse.isSuccess()){  // 如果失败
            return OrderItemListServerResponse;
        }
        List<OrderItem> orderItemList=(List<OrderItem>)OrderItemListServerResponse.getData();
        // orderItemList批量插入数据库
        orderItemMapper.batchInsert(orderItemList);

        // 计算总价格
        BigDecimal totalPrice= BigDecimal.valueOf(0);
        for(int i=0;i<orderItemList.size();i++){
            totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(), orderItemList.get(i).getTotalPrice().doubleValue()); // 累加所有商品总价
//            int cowCunt=orderItemMapper.insert(orderItemList.get(i));  // 将orderItem插入数据库
        }

        // 生成订单order
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingAddressId);
        order.setPayment(totalPrice);
        order.setPaymentType(Const.OrderPayTypeEnum.online.getCode());  // '支付类型,1-在线支付'
        order.setPostage(0);  // 运费0，要是有运费值，也可以设置
        order.setStatus(Const.orderStatusEnum.waitPay.getCode());  // 创建订单后，订单状态为：10-未付款
        int crowCunt=orderMapper.insert(order);  // 插入数据库
        if(crowCunt<=0){
            return ServerResponse.createErrorMessage("数据库插入异常，生成订单失败");
        }
        // 订单生成成功后，减少商品库存
        for(int i=0;i<cartList.size();i++){
            int productId=cartList.get(i).getProductId();
            Product product=productMapper.selectByPrimaryKey(productId);
            product.setStock(product.getStock()-cartList.get(i).getQuantity());
            productMapper.updateByPrimaryKeySelective(product);  // 更新product
        }
        // 订单生成成功后，清除购物车
        cleanCart(cartList);

        //组装返回给前端的信息orderVo
        List<OrderItemVo> orderItemVoList=new ArrayList<>();
        System.out.println(orderItemList);
        for(int i=0;i<orderItemList.size();i++){
            OrderItemVo orderItemVo=setOrderItemVo(orderItemList.get(i));
            orderItemVoList.add(orderItemVo);
        }
        ShippingAddress shippingAddress=shippingMapper.selectByPrimaryKey(shippingAddressId);
        if(shippingAddress==null){
            return ServerResponse.createErrorMessage("收货地址ID不对，找不到收货地址");
        }
        ShippingAddressVo shippingAddressVo=setShippingAddressVo(shippingAddress);
        Order order1=orderMapper.selectByOrderNo(orderNo);
        OrderVo orderVo=setOrderVO(order1,orderItemVoList,shippingAddressVo,shippingAddressId);
        return ServerResponse.createSuccessMsgData("生成订单成功,返回orderVo",orderVo);
    }

    public ServerResponse setOrderItemList(List<Cart> cartList,long orderNo,int userId){
        List<OrderItem> orderItemList=new ArrayList<>();
        for(int i=0;i<cartList.size();i++){
            OrderItem orderItem=new OrderItem();  // 订单中商品明细
            int productId=cartList.get(i).getProductId();
            Product product=productMapper.selectByPrimaryKey(productId);
            if(product.getStatus()!=Const.productStatusEnum.onSale.getCode()){  // 如果商品不是在售状态
                return ServerResponse.createErrorMessageData("该商品:"+product.getName()+" 已下架",product.getId());
            }
            if(cartList.get(i).getQuantity()>product.getStock()){   // 如果下单数量 > 库存
                return ServerResponse.createErrorMessageData("该商品:"+product.getName()+" 库存不足",product.getId());
            }

            orderItem.setOrderNo(orderNo);
            orderItem.setUserId(userId);
            orderItem.setProductId(productId);
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());  // 记录下单时商品的价格
            orderItem.setQuantity(cartList.get(i).getQuantity());
            BigDecimal totalPrice=BigDecimalUtil.mul(product.getPrice().doubleValue(),cartList.get(i).getQuantity()); // 单价*数量
            orderItem.setTotalPrice(totalPrice);

            orderItemList.add(orderItem);
        }

        return ServerResponse.createSuccessData(orderItemList);
    }

    public long generateOrderNo(){
        long currentTime=System.currentTimeMillis();  // 产生一个当前的毫秒，这个毫秒其实就是自1970年1月1日0时起的毫秒数
        Random rd = new Random();
        currentTime=currentTime+rd.nextInt(100);  // 系统时间+一个0~99的随机
        return currentTime;
    }

    public void cleanCart(List<Cart> cartList){
        List<Integer> cartIdList=new ArrayList();
        for(int i=0;i<cartList.size();i++){
            cartIdList.add(cartList.get(i).getId());
        }
        // mybatis 批量删出
        cartMapper.batchDeleteById(cartIdList);

    }

    public OrderVo setOrderVO(Order order, List<OrderItemVo> orderItemVoList, ShippingAddressVo shippingAddressVo,int shippingAddressId){
        OrderVo orderVo=new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.OrderPayTypeEnum.getOrderPayTypeEnumByCode(order.getPaymentType()).getInfo());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.orderStatusEnum.getOrderStatusEnumByCode(order.getStatus()).getInfo());
        orderVo.setPaymentTime(dateConvertUtil.dateToString(order.getPaymentTime(),"yyyy-MM-dd HH:mm:ss"));
        orderVo.setSendTime(dateConvertUtil.dateToString(order.getSendTime(),"yyyy-MM-dd HH:mm:ss"));
        orderVo.setEndTime(dateConvertUtil.dateToString(order.getEndTime(),"yyyy-MM-dd HH:mm:ss"));
        orderVo.setCloseTime(dateConvertUtil.dateToString(order.getCloseTime(),"yyyy-MM-dd HH:mm:ss"));
        orderVo.setCreateTime(dateConvertUtil.dateToString(order.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));

        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setImageHost(ftpPropertiesUtil.getValue("ftp.server.http.prefix"));

        orderVo.setShippingAddressId(shippingAddressId);
        if(shippingAddressVo!=null){
            orderVo.setReceiverName(shippingAddressVo.getReceiverName());
            orderVo.setShippingAddressVo(shippingAddressVo);
        }

        return orderVo;
    }

    public OrderItemVo setOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo=new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(dateConvertUtil.dateToString(orderItem.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));

        return orderItemVo;
    }

    public ShippingAddressVo setShippingAddressVo(ShippingAddress shippingAddress){
        if(shippingAddress==null){
            return null;
        }
        ShippingAddressVo shippingAddressVo=new ShippingAddressVo();
        shippingAddressVo.setReceiverName(shippingAddress.getReceiverName());
        shippingAddressVo.setReceiverPhone(shippingAddress.getReceiverPhone());
        shippingAddressVo.setReceiverMobile(shippingAddress.getReceiverMobile());
        shippingAddressVo.setReceiverProvince(shippingAddress.getReceiverProvince());
        shippingAddressVo.setReceiverCity(shippingAddress.getReceiverCity());
        shippingAddressVo.setReceiverDistrict(shippingAddress.getReceiverDistrict());
        shippingAddressVo.setReceiverAddress(shippingAddress.getReceiverAddress());
        shippingAddressVo.setReceiverZip(shippingAddress.getReceiverZip());

        return shippingAddressVo;
    }


    /**
     * @Description:  查询用户的所有订单
     * @param userId
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse getOderList(int userId){
        List<Order> orderList=orderMapper.selectOrderListByUserId(userId);
        if(orderList==null){
            return ServerResponse.createSuccessMsg("该用户没有任何订单");
        }
        List<OrderVo> orderVoList=new ArrayList<>();
        for(int i=0;i<orderList.size();i++){
            List<OrderItemVo> OrderItemVoList=new ArrayList<>();
            long orderNo=orderList.get(i).getOrderNo();
            List<OrderItem> orderItemList=orderItemMapper.selectByOrderNoAndUserId(orderNo,userId);
            for(int j=0;j<orderItemList.size();j++){
                OrderItemVo orderItemVo=setOrderItemVo(orderItemList.get(j));
                OrderItemVoList.add(orderItemVo);
            }
            ShippingAddress shippingAddress=shippingMapper.selectByPrimaryKey(orderList.get(i).getShippingId());
            ShippingAddressVo shippingAddressVo=setShippingAddressVo(shippingAddress);
            OrderVo orderVo=setOrderVO(orderList.get(i),OrderItemVoList,shippingAddressVo,orderList.get(i).getShippingId());

            orderVoList.add(orderVo);
        }

        return ServerResponse.createSuccessMsgData("用户订单查询成功",orderVoList);
    }

    /**
     * @Description:   查询超时、未付款订单
     * @param
     * @return: void
     */
    public List selectOrderNeedClose(String closeTime){
        List<Order> list;
        list=orderMapper.selectOrderNeedClose(closeTime,Const.orderStatusEnum.waitPay.getCode());
        return list;
    }

    /**
     * @Description:  关闭超时订单
     * @param
     * @return: void
     */
    public void closeOrder(){
        Date now = new Date();
        long nowTime=now.getTime();
        long currentTime = (long)(nowTime - Const.maxOrderWaitPayHours * 60 * 60 * 1000);
        Date closeDate = new Date(currentTime);
        List<Order> list=selectOrderNeedClose(dateConvertUtil.dateToString(closeDate,"yyyy-MM-dd HH:mm:ss"));
        for(Order order:list){
            List<OrderItem> orderItemList=iOrderItemService.selectByOrderNo(order.getOrderNo());
            for(OrderItem orderItem:orderItemList){
                Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
                // 产品返回库存
                int stock=product.getStock()+orderItem.getQuantity();
                Product newProduct=new Product();
                newProduct.setId(product.getId());
                newProduct.setStock(stock);
                productMapper.updateByPrimaryKeySelective(newProduct);
            }
            // 删出订单 (订单状态置为：取消)
            orderMapper.cancelOrderById(Const.orderStatusEnum.cancel.getCode(),order.getId());
        }
    }


}
