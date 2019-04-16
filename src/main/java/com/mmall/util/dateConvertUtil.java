package com.mmall.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:  date转string、string转date
 * @author: cls
 **/
public class dateConvertUtil {

    public static String dateToString(Date date,String stringFormat){
        if(date==null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(stringFormat);
        String str=sdf.format(date);
        return str;
    }

    public static Date stringToDate(String dateStr,String stringFormat) throws ParseException {
        if(dateStr==null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(stringFormat);
        return sdf.parse(dateStr);
    }

    @Test
    public void test() throws ParseException {
        Date date=new Date();
        String str="2019-04-01 21:04:40";
        System.out.println(dateToString(date,"yyyy-MM-dd HH:mm:ss"));  // "yyyy-MM-dd HH:mm:ss" 大小写不能乱改
        System.out.println(stringToDate(str,"yyyy-MM-dd HH:mm:ss"));
    }

}
