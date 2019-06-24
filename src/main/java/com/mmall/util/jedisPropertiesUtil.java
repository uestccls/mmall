package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @description:  读取配置文件
 * @author: cls
 **/
public class jedisPropertiesUtil {

    private static Logger logger= LoggerFactory.getLogger(jedisPropertiesUtil.class);
    private static Properties p;
    static {               // 静态代码块 随着类的加载而加载（只执行一次）  读取静态文件
        p=new Properties();
        InputStream inputStream=jedisPropertiesUtil.class.getClassLoader().getResourceAsStream("jedis.properties");
        try {
            p.load(inputStream);
        } catch (IOException e) {
            logger.error("ftpProperties配置文件读取异常",e);
        }
    }

    public static String getValue(String key){
        String value=p.getProperty(key);
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value;
    }


}
