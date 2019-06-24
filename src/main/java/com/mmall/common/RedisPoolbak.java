package com.mmall.common;

import com.mmall.util.jedisPropertiesUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @description:
 * @author: cls
 **/
public class RedisPoolbak {

    private static JedisPool jedisPool;  // jedis连接池
    private static int maxTotal= Integer.parseInt(jedisPropertiesUtil.getValue("redis.maxTotal")); // 最大连接数
    private static int maxIdle=Integer.parseInt(jedisPropertiesUtil.getValue("redis.maxIdle")); // 最大空闲连接数
    private static int minIdle=Integer.parseInt(jedisPropertiesUtil.getValue("redis.minIdle")); // 最小连接数
    private static boolean testOnBorrow= Boolean.parseBoolean(jedisPropertiesUtil.getValue("redis.test.borrow")); // 从redis连接池中获取连接时，校验并返回可用的连接
    private static boolean testOnReturn= Boolean.parseBoolean(jedisPropertiesUtil.getValue("redis.test.return")); // #把连接放回redis连接池中时，校验并返回可用的连接
    private static String redisIp=jedisPropertiesUtil.getValue("redis.ip");
    private static int redisPort=Integer.parseInt(jedisPropertiesUtil.getValue("redis.port"));
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
        jedisPool=new JedisPool(config,redisIp,redisPort,maxWaitMillis);  //创建连接池实例
    }

    // 静态代码初始化池配置
    static {
        JedisPoolInit();
    }

    /**获得jedis对象*/
    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    // 归还jedis对象
    public static void returnResource(Jedis jedis){
        jedisPool.returnResource(jedis);
    }
    // 归还坏的连接
    public static void returnBrokenResource(Jedis jedis){
        jedisPool.returnBrokenResource(jedis);
    }

    @Test
    public void test(){
        Jedis jedis=getJedis();   // 获得一个连接
        jedis.set("cls","ff");   // 存入一个字符串值
        returnResource(jedis);  // 归还连接
        jedisPool.destroy();   // 销毁连接池

        System.out.println("endendend!");
    }

}
