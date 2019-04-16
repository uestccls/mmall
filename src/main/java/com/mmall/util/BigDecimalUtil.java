package com.mmall.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description:  BigDecimal + - * \
 * @author: cls
 **/
public class BigDecimalUtil {

    public static BigDecimal add(double d1,double d2){  // +
        BigDecimal b1=BigDecimal.valueOf(d1);
        BigDecimal b2=BigDecimal.valueOf(d2);
        return b1.add(b2);
//        return b1.add(b2).setScale(2, RoundingMode.HALF_UP);  // 四舍五入 保留两位小数（类型为BigDecimal，当转化成double后：0.00会变成0.0）
    }

    public static BigDecimal sub(double d1,double d2){  // -
        BigDecimal b1=BigDecimal.valueOf(d1);
        BigDecimal b2=BigDecimal.valueOf(d2);
        return b1.subtract(b2);
    }

    public static BigDecimal mul(double d1,double d2){  // *
        BigDecimal b1=BigDecimal.valueOf(d1);
        BigDecimal b2=BigDecimal.valueOf(d2);
        return b1.multiply(b2);
    }

    public static BigDecimal div(double d1,double d2,int len){  // 除 四舍五入 保留 len位小数
        BigDecimal b1=BigDecimal.valueOf(d1);
        BigDecimal b2=BigDecimal.valueOf(d2);
        return b1.divide(b2,len,BigDecimal.ROUND_HALF_UP);
    }

    @Test
    public void test(){
        System.out.println(add(1.221,2.761));
//        double a=1.0000;
//        System.out.println(a);
    }

}



