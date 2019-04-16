package com.mmall.vo;

import java.io.Serializable;

/**
 * @description:
 * @author: cls
 **/
public class PayPreCreateVo {

    private String out_trade_no;  // 订单编号
    private String qr_code;      // 二维码链接
    private String sign;       // 签名

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "PayPreCreateVo{" +
                "out_trade_no='" + out_trade_no + '\'' +
                ", qr_code='" + qr_code + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

}
