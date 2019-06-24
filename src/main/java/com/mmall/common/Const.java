package com.mmall.common;

/**
 * @description:
 * @author: cls
 * @create: 2019-02-23 11:21
 **/
public class Const {
   public static final String Current_User="currentUser";
   public static final int Role_Admin=1;  // 管理员
   public static final int Role_Customer=0;  // 普通用户
   public static final String Type_Username="username";
   public static final int cartProductChecked=1;  // 购物车中商品选中状态
   public static final int cartProductUnChecked=0;  // 未选中状态

   public static final double maxOrderWaitPayHours= 0.5;  // 定时关单时间
   public static final String redisClosingOrderLock= "redisClosingOrderLock";  // 定时关单redis分布式锁

   public static enum orderStatusEnum{
      cancel(0,"cancel"),
      waitPay(10,"waitPay"),
      alreadyPay(20,"alreadyPay"),
      alreadySend(40,"alreadySend"),  // 已发货
      tradeSuccess(50,"tradeSuccess"),
      tradeClose(60,"tradeClose");

      int code;
      String info;
      private orderStatusEnum(int code,String info){
         this.code=code;
         this.info=info;
      }
      public static orderStatusEnum getOrderStatusEnumByCode(int code){
         for(orderStatusEnum oRderPayTypeEnum : orderStatusEnum.values()){
            if(oRderPayTypeEnum.getCode()==code){
               return oRderPayTypeEnum;
            }
         }
         throw new RuntimeException("没有找到code值对应的枚举类");
      }

      public int getCode() {
         return code;
      }
      public String getInfo() {
         return info;
      }
   }

   public interface alipayCallBack{
       String tradeStatusTRADE_SUCCESS="TRADE_SUCCESS";
       String tradeStatusWAIT_BUYER_PAY="WAIT_BUYER_PAY";

       String responseSuccess="success";
       String responseFailed="failed";

   }

   public interface redis{
      int redisSessionTime=60*30;  // redis中Session存储有效时间  30分钟 （单位：秒）
      String token_prefix="token_";

   }



   public static enum pay_platformEnum{
      alipay(1,"zhifubao"),
      weiXin(2,"weiXin");

      int code;
      String info;
      private pay_platformEnum(int code,String info){
         this.code=code;
         this.info=info;
      }

      public int getCode() {
         return code;
      }
      public String getInfo() {
         return info;
      }
   }

   public static enum productStatusEnum{
      onSale(1,"onSale"),
      soldOut(2,"soldOut"),  // 下架
      delete(3,"delete");

      int code;
      String info;
      private productStatusEnum(int code,String info){
         this.code=code;
         this.info=info;
      }

      public int getCode() {
         return code;
      }
      public String getInfo() {
         return info;
      }
   }

   public static enum OrderPayTypeEnum{
      online(1,"在线支付");

      int code;
      String info;
      private OrderPayTypeEnum(int code,String info){
         this.code=code;
         this.info=info;
      }
      public static OrderPayTypeEnum getOrderPayTypeEnumByCode(int code){
         for(OrderPayTypeEnum orderPayTypeEnum:OrderPayTypeEnum.values()){
            if(orderPayTypeEnum.getCode()==code){
               return orderPayTypeEnum;
            }
         }
         throw new RuntimeException("没有找到code值对应的枚举类");
      }

      public int getCode() {
         return code;
      }
      public String getInfo() {
         return info;
      }
   }







}
