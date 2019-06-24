package com.mmall.util;

import com.mmall.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:  在日常开发中，经常利用JSON作为数据传输的格式，为此JSON序列化(将对象转换为JSON字符串)和反序列化(将JSON字符串转换指定的数据类型)经常用到。
 * @author: cls
 **/
public class JsonUtil {

    private static Logger logger= LoggerFactory.getLogger(JsonUtil.class);

    // ObjectMapper是JSON操作的核心，Jackson的所有JSON操作都是在ObjectMapper中实现。
    private static ObjectMapper objectMapper=new ObjectMapper();
    // 属性配置
    static {
        // 对象的所有字段全部列入序列化
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        // 取消默认转换TIMESTAMPS形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
        // 忽略空bean转jason的错误    (false 让它出现空时报错，默认true)
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        // 所有的日期格式都统一成 yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况。
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }


    /**
     * @Description: 将对象序列化成jason序列，并把结果输出成字符串。
     * @param obj
     * @return: java.lang.String
     */
    public static <T> String objToString(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            logger.warn("objToString:{} error",obj,e);
            return null;
        }
    }

    /**
     * @Description:  比 objToString 格式好看
     * @param obj
     * @return: java.lang.String
     */
    public static <T> String objToStringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            logger.warn("objToString:{} error",obj,e);
            return null;
        }
    }

    /**
     * @Description:  反序列化
     * @param str
     * @param clazz
     * @return: T
     */
    public static <T> T stringToObj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)? (T)str: objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            logger.warn("stringToObj反序列化 error",e);
            return null;
        }
    }

    /**
     * @Description:   用于反序列化复杂对象 如 List<User> 集合
     * @param str
     * @param tTypeReference
     * @return: T
     */
    public static <T> T stringToObj(String str,TypeReference<T> tTypeReference){
        if(StringUtils.isEmpty(str)||tTypeReference==null){
            return null;
        }
        try {
            return (T)(tTypeReference.getType().equals(String.class)? str: objectMapper.readValue(str,tTypeReference));
        } catch (IOException e) {
            logger.warn("stringToObj反序列化 error",e);
            return null;
        }
    }

    /**
     * @Description:   用于反序列化复杂对象 如 List<User>、集合
     * @param str
     * @param
     * @return: T
     */
    public static <T> T stringToObj(String str,Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            logger.warn("stringToObj反序列化 error",e);
            return null;
        }
    }

    @Test
    public void test(){
        User user1=new User();
        user1.setId(1);
        user1.setUsername("ff");
        User user2=new User();
        user2.setId(2);
        user2.setUsername("qq");
//        String r1=JsonUtil.objToString(user1);
        String r2=JsonUtil.objToStringPretty(user1);
//        System.out.println(r1);
//        System.out.println(r2);
        User user=JsonUtil.stringToObj(r2,User.class);

        List<User> userList=new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        String result3=JsonUtil.objToStringPretty(userList);
//        System.out.println(result3);
        List<User> userList2=JsonUtil.stringToObj(result3, new TypeReference<List<User>>() {
        });
        List<User> userList3=JsonUtil.stringToObj(result3,List.class,User.class);

        System.out.println(userList2.get(0).getId());

    }


}
