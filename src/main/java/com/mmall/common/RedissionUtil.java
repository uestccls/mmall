package com.mmall.common;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: cls
 **/
public class RedissionUtil {

//    private static Logger logger = LoggerFactory.getLogger(RedissionUtil.class);
    public static RedissonClient redisson;    // redission客户端

    public static void init(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");  //单机

        redisson= Redisson.create(config);
    }

    static {
        init();
    }

    /**
     * @Description:  获取锁
     * @param keyName
     * @return: org.redisson.api.RLock
     */
    public static RLock getRLock(String keyName){
        RLock rLock = redisson.getLock(keyName);
        return rLock;

    }




}
