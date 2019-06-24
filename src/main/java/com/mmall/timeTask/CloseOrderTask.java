package com.mmall.timeTask;

import com.mmall.common.Const;
import com.mmall.common.RedissionUtil;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.Product;
import com.mmall.service.IOrderService;
import com.mmall.service.IOrderItemService;
import com.mmall.service.IProductService;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.util.dateConvertUtil;
import com.mmall.util.jedisPropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: cls
 **/
@Lazy(false)
@Component
public class CloseOrderTask {
    private static Logger logger = LoggerFactory.getLogger(CloseOrderTask.class);

    @Autowired
    IOrderService iOrderService;
    @Autowired
    IOrderItemService iOrderItemService;
    @Autowired
    IProductService iProductService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;

   /**
    * @Description:  定时关闭未付款订单，每一分钟扫描一次   关单方式采用轮询（周期1min），查找数据库中未付款且超时的
    * @param
    * @return:  void
    */
//    @Scheduled(cron="0 */1 * * * ?")  // 1min
    public void closeOrderV1() {
        logger.info("正在关闭超时未付款订单...");
        iOrderService.closeOrder();
        logger.info("超时未付款订单已关闭！！！");
    }

    /**
     * @Description:  使用redis实现分布式锁
     * @param
     * @return: void
     */
    @Scheduled(cron="*/5 * * * * ? ")  // 5s
    public void closeOrderV2(){
        logger.info("正在关闭超时未付款订单...");
        // 尝试获取锁   value:  当前时间+过期超时时间   （过期时间 必须保证业务逻辑能够执行完）
        Long Time=System.currentTimeMillis()+Long.valueOf(jedisPropertiesUtil.getValue("redisClosingOrderLockExpireTime"))*1000;
        long result= RedisShardedPoolUtil.setNx(Const.redisClosingOrderLock, String.valueOf(Time));
        if(result==1){
            logger.info("线程: {} ，获取锁：{} 成功!!!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
            // 成功则，设置超时时间，为的是避免死锁问题
            RedisShardedPoolUtil.expire(Const.redisClosingOrderLock, Integer.parseInt(jedisPropertiesUtil.getValue("redisClosingOrderLockExpireTime")));
            // 执行逻辑
            iOrderService.closeOrder();
            // 释放锁
            String LockTime= RedisShardedPoolUtil.get(Const.redisClosingOrderLock);
            if(Long.valueOf(LockTime)>System.currentTimeMillis()){   // 锁时间>当前时间，释放锁
                RedisShardedPoolUtil.del(Const.redisClosingOrderLock);
            }
            logger.info("超时未付款订单已关闭！！！");

        }else {
            // 失败则，查看当前占有的锁是否超时
            String oldTime=RedisShardedPoolUtil.get(Const.redisClosingOrderLock);
            if(oldTime==null || Long.valueOf(oldTime)<System.currentTimeMillis()){  // 锁已超时，可以允许别的请求重新获取
                long newExpireTime=System.currentTimeMillis()+Long.valueOf(jedisPropertiesUtil.getValue("redisClosingOrderLockExpireTime"))*1000;
                String oldExpireTime=RedisShardedPoolUtil.getSet(Const.redisClosingOrderLock,String.valueOf(newExpireTime));
                if(oldExpireTime==null||StringUtils.equals(oldTime,oldExpireTime)){
                    // 获取锁成功
                    logger.info("线程: {} ，获取锁：{} 成功!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
                    // 成功则，设置超时时间，为的是避免死锁问题
                    RedisShardedPoolUtil.expire(Const.redisClosingOrderLock, Integer.parseInt(jedisPropertiesUtil.getValue("redisClosingOrderLockExpireTime")));
//                    iOrderService.closeOrder();
                    // 释放锁
                    String LockTime= RedisShardedPoolUtil.get(Const.redisClosingOrderLock);
                    if(Long.parseLong(LockTime)>System.currentTimeMillis()){   // 锁时间>当前时间，释放锁
                        RedisShardedPoolUtil.del(Const.redisClosingOrderLock);
                    }
                    logger.info("超时未付款订单已关闭！");
                }else{
                    logger.info("线程: {} ，获取锁：{} 失败!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
                }
            }else {
                logger.info("线程: {} ，获取锁：{} 失败!!!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
            }
        }
    }

    /**
     * @Description:  使用redission实现分布式锁
     * @param
     * @return: void
     */
//    @Scheduled(cron="*/5 * * * * ? ")  // 5s
    public void closeOrderV3() throws InterruptedException {
        RLock lock= RedissionUtil.getRLock(Const.redisClosingOrderLock);
        boolean res=lock.tryLock(0, 5, TimeUnit.SECONDS);  // 等待0s（不等待，很重要，否则在等待时间内,可能另一个线程释放了锁,则该线程又会获得锁），占有锁时间5s
        if(res){
            try {
                logger.info("线程: {} ，获取锁：{} 成功!!!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
                // 执行逻辑
//            iOrderService.closeOrder();
            } finally {
                lock.unlock();
                logger.info("线程: {} ，释放锁：{} !!!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
            }
        }else {
            logger.info("线程: {} ，获取锁：{} 失败!!!",Thread.currentThread().getName(),Const.redisClosingOrderLock);
        }

    }


    @Test
    public void test(){
        Date date1=new Date(System.currentTimeMillis());
        System.out.println(date1);
        Long Time=System.currentTimeMillis()+Long.valueOf(jedisPropertiesUtil.getValue("redisClosingOrderLockExpireTime"));
        Date date2=new Date(Time);
        System.out.println(date2);

    }

}
