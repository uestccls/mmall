package com.mmall.common;

/**
 * @description:
 * @author: cls
 * @create: 2019-02-22 15:10
 **/
public enum ResponseCode {

    /* 枚举类的所有实例必须在第一行显示列出
       枚举类一旦显示定义了带参数的构造器，列举枚举值时就必须传入对应参数
   */
    SUCCESS(0,"success"),
    ERROR(1,"error"),
    Need_Login(10,"need_login"),
    Illegal_Argument(12,"参数错误");

    private final int code;
    private final String info;

//    枚举类的构造器只能使用private修饰
    private ResponseCode(int code,String info){
        this.code=code;
        this.info=info;
    }

    public int getCode() {
        return code;
    }
    public String getInfo(){
        return info;
    }

}
