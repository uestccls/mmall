package com.mmall.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description:  MD5加密工具类
 * @author: cls
 **/
public class MD5Util {

    private static final String[] hex=new String[]{"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * @Description: 将字节数组转换成0-f的字符串
     * @param bytes
     * @return: java.lang.String
     */
    private static String byteArrayToHexString(byte[] bytes){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<bytes.length;i++){
            sb.append(byteToHexString(bytes[i]));
        }
        return sb.toString();
    }
    /**
     * @Description: 将单个字节转换成0-f的字符串
     * @param b
     * @return: java.lang.String
     */
    private static String byteToHexString(byte b){
        int n=b;
        int i,j;
        if(n<0){
            n=n+255;
        }
        i=n/16;
        j=n%16;
        return hex[i]+hex[j]; // 字符串常量的+操作其本质是new了StringBuilder对象进行append操作，拼接后调用toString()返回String对象　　
    }

    /**
     * @Description:  MD5加密  返回大写MD5
     * @param origin
     * @param charsetname
     * @return: java.lang.String
     */
    private static String MD5Encode(String origin,String charsetname){
        String resultString=null;
        try {
            resultString=new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if(charsetname==null | "".equals(charsetname)){
                resultString=byteArrayToHexString(md.digest(resultString.getBytes()));
            }else {
                resultString=byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    /**
     * @Description:  public方法 固定编码：UTF-8
     * @param origin
     * @return: java.lang.String
     */
    public static String MD5EncodeUtf8(String origin){
        return MD5Encode(origin,"UTF-8");
    }

}
