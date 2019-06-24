package com.mmall.common;

import com.mmall.util.jedisPropertiesUtil;
import org.junit.Test;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description:   redis集群 采用客户端分片方式
 * @author: cls
 **/

public class ShardedJedisPool {

    private static redis.clients.jedis.ShardedJedisPool shardedJedisPool;  // shardedJedis连接池
    private static int maxTotal= Integer.parseInt(jedisPropertiesUtil.getValue("redis.maxTotal")); // 最大连接数
    private static int maxIdle=Integer.parseInt(jedisPropertiesUtil.getValue("redis.maxIdle")); // 最大空闲连接数
    private static int minIdle=Integer.parseInt(jedisPropertiesUtil.getValue("redis.minIdle")); // 最小连接数
    private static boolean testOnBorrow= Boolean.parseBoolean(jedisPropertiesUtil.getValue("redis.test.borrow")); // 从redis连接池中获取连接时，校验并返回可用的连接
    private static boolean testOnReturn= Boolean.parseBoolean(jedisPropertiesUtil.getValue("redis.test.return")); // #把连接放回redis连接池中时，校验并返回可用的连接
    private static String redis1Ip=jedisPropertiesUtil.getValue("redis1.ip");
    private static int redis1Port=Integer.parseInt(jedisPropertiesUtil.getValue("redis1.port"));
    private static String redis2Ip=jedisPropertiesUtil.getValue("redis2.ip");
    private static int redis2Port=Integer.parseInt(jedisPropertiesUtil.getValue("redis2.port"));
    private static int maxWaitMillis=Integer.parseInt(jedisPropertiesUtil.getValue("redis.maxWaitMillis"));

    /**
     * @Description:  初始化连接池
     * @param
     * @return: void
     */
    private static void JedisPoolInit(){
        //创建jedis池配置实例
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true); // 连接耗尽时，是否阻塞
        // redis集群 采用客户端分片方式  两个客户端信息
        JedisShardInfo shardInfo1=new JedisShardInfo(redis1Ip,redis1Port,maxWaitMillis);
        JedisShardInfo shardInfo2=new JedisShardInfo(redis2Ip,redis2Port,maxWaitMillis);
        //初始化ShardedJedisPool
        List<JedisShardInfo> ShardInfoList =new ArrayList();
        ShardInfoList.add(shardInfo1);
        ShardInfoList.add(shardInfo2);
        shardedJedisPool=new redis.clients.jedis.ShardedJedisPool(config,ShardInfoList,Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);  //创建连接池实例
    }

    // 静态代码初始化池配置
    static {
        JedisPoolInit();
    }

    /**获得jedis对象*/
    public static ShardedJedis  getJedis(){
        return shardedJedisPool.getResource();
    }

    // 归还jedis对象
    public static void returnResource(ShardedJedis jedis){
        shardedJedisPool.returnResource(jedis);
    }
    // 归还坏的连接
    public static void returnBrokenResource(ShardedJedis jedis){
        shardedJedisPool.returnBrokenResource(jedis);
    }

    @Test
    public void test(){
        ShardedJedis jedis=getJedis();   // 获得一个连接
        for(int i=0;i<10;i++){
            jedis.set("key"+String.valueOf(i),String.valueOf(i));   // 存入一个字符串值  (对于数字，hash一致性算法好像分布不均匀)
        }
        returnResource(jedis);  // 归还连接
//        shardedJedisPool.destroy();   // 销毁连接池

        System.out.println("endendend!");
    }

}
