package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @description: token本地缓存，使用guava缓存实现
 * @author: cls
 **/
public class TokenCache {

    public static final String token_prefix="token_";
    //    创建logback的logger
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //    声明一个静态的内存块,guava里面的本地缓存
    public static LoadingCache<String,String> localCache= CacheBuilder.newBuilder().initialCapacity(1000)
            //构建本地缓存，调用链的方式 ,1000是设置缓存的初始化容量，maximumSize是设置缓存最大容量，当超过了最大容量，guava将使用LRU算法（最少使用算法），来移除缓存项
            //expireAfterAccess(12,TimeUnit.HOURS)设置缓存有效期为12个小时
            .maximumSize(10000).refreshAfterWrite(10, TimeUnit.HOURS).build(
               //这个方法是默认的数据加载实现,get的时候，如果key没有对应的值，就调用这个方法进行加载
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String s) throws Exception {
                            // 为什么要把return的null值写成字符串，因为到时候用null去.equal的时候，会报空指针异常
                            return "null";
                        }
                    });
    /**
     * @Description: set方法 添加本地缓存
     * @param key
     * @param value
     * @return: void
     */
     public static void setKey(String key,String value){
         localCache.put(key,value);
     }

     /**
      * @Description: get方法 获取本地缓存
      * @param key
      * @return: java.lang.String
      */
     public static String getValue(String key){
         String value = null;
         try {
             value= localCache.get(key);
             if ("null".equals(value)) {
                 return null;
             }
             return value;
         } catch (ExecutionException e) {
             logger.error("getValue()方法错误",e);
         }
         return null;
     }


}
