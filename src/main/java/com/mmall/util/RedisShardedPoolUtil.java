package com.mmall.util;

import com.mmall.common.ShardedJedisPool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

/**
 * @description:  RedisPool工具，封装jedis的set、get等操作数据方法
 * @author: cls
 **/
public class RedisShardedPoolUtil {

    private static Logger logger= LoggerFactory.getLogger(RedisShardedPoolUtil.class);

    /**
     * @Description:  同jedis的set操作
     * @param key
     * @param value
     * @return: java.lang.String
     */
    public static String set(String key,String value){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.set(key,value);
        }catch (Exception e){
            logger.error("set key:{} value:{} error",key,value,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    /**
     * @Description:    同jedis的setex操作
     * @param key
     * @param value
     * @param exTime  秒
     * @return: java.lang.String
     */
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.setex(key,exTime,value);
        }catch (Exception e){
            logger.error("setex key:{} exTime:{} value:{} error",key,exTime,value,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    /**
     * @Description:  设置key的有效期
     * @param key
     * @param exTime  秒
     * @return: long
     */
    public static long expire(String key,int exTime){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.expire(key,exTime);
        }catch (Exception e){
            logger.error("expire key:{} exTime:{} error",key,exTime,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.get(key);
        }catch (Exception e){
            logger.error("get key:{} error",key,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.del(key);
        }catch (Exception e){
            logger.error("del key:{} error",key,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    public static Long setNx(String key,String value){
        ShardedJedis jedis=null;
        Long result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.setnx(key,value);
        }catch (Exception e){
            logger.error("set key:{} value:{} error",key,value,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    public static String getSet(String key,String value){
        ShardedJedis jedis=null;
        String result=null;
        try {
            jedis= ShardedJedisPool.getJedis();
            result=jedis.getSet(key,value);
        }catch (Exception e){
            logger.error("set key:{} value:{} error",key,value,e);
            ShardedJedisPool.returnBrokenResource(jedis);
            return result;
        }
        ShardedJedisPool.returnResource(jedis);
        return result;
    }

    @Test
    public void test(){
        RedisShardedPoolUtil.set("key1test","value1");
        String value= RedisShardedPoolUtil.get("key1test");
        RedisShardedPoolUtil.setEx("keyex1","valueex",60*5);
        RedisShardedPoolUtil.setEx("keyex2","valueex",60*5);
        RedisShardedPoolUtil.setEx("keyex3","valueex",60*5);
        RedisShardedPoolUtil.expire("key1test",60*4);
//        RedisShardedPoolUtil.del("key1test");

        System.out.println("endend! "+value);

    }


}
