package com.mmall.service.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.mmall.alipay.trade.PayAction;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.service.IPayInfoService;
import com.mmall.util.FtpUtil;
import com.mmall.util.dateConvertUtil;
import com.mmall.util.ftpPropertiesUtil;
import com.mmall.vo.PayPreCreateVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.image.BufferedImage;

/**
 * @description:
 * @author: cls
 **/
@Service("iPayInfoService")
public class IPayInfoServiceImpl implements IPayInfoService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    PayInfoMapper payInfoMapper;

    /**
     * @Description: 生成支付二维码
     * @param
     * @return: com.mmall.common.ServerResponse<com.mmall.vo.PayPreCreateVo>
     */
    @Override
    public ServerResponse<PayPreCreateVo> pay(int userId,long orderNo,String path) throws IOException {
        Order order=orderMapper.selectByOrderNo(orderNo);  // 根据订单编号查询订单
        List<OrderItem> orderItemList=orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(),userId);
//        if(userId!=order.getUserId()){  // 登录用户与订单用户不一致
//            return ServerResponse.createErrorMessage("登录用户与订单用户不一致");
//        }
        AlipayF2FPrecreateResult result=PayAction.trade_precreate(order,orderItemList);  // 生成付款二维码连接
//        System.out.println("AlipayF2FPrecreateResult: "+result);
        AlipayTradePrecreateResponse preCreateResponse=result.getResponse();  // 获得返回结果响应
//        System.out.println(preCreateResponse.getParams());
//        System.out.println(preCreateResponse.getMsg());
        switch (result.getTradeStatus()){
            case SUCCESS:
                PayPreCreateVo payPreCreateVo=setPayPreCreateVo(preCreateResponse.getBody());

                // 需要修改为运行机器上的路径
                path=path+"MyMmallQRCode";   // 设置二维码存放路径
                File file=new File(path);
                if(!file.exists()){  // 如果该目录不存在
                    file.mkdir();   // 创建 QRCode文件夹
                }
                String filePath = String.format(path+"/qr-%s.png",preCreateResponse.getOutTradeNo());
                //生成二维码，并保存到 filePath
                File QRCodeImgFile=ZxingUtils.getQRCodeImge(preCreateResponse.getQrCode(), 256, filePath);

                // 将二维码上传到FTP服务器
                Map<String,String>urMap = new HashMap();
                // 读取ftp配置信息
                String ftpIp = ftpPropertiesUtil.getValue("ftp.ftpServerIp");
                int ftpPort = Integer.parseInt(ftpPropertiesUtil.getValue("ftp.ftpServerPort"));
                String ftpUsername = ftpPropertiesUtil.getValue("ftp.ftpUsername");
                String ftpPassword = ftpPropertiesUtil.getValue("ftp.ftpPassword");

                String upPath = "images/MmallQRCode/";  // 设置上传路径
                String filename = QRCodeImgFile.getName();  //上传文件名
                String suffix = filename.substring(filename.lastIndexOf(".")); // 获取文件名后缀
                filename = UUID.randomUUID().toString() + suffix;  // 重置文件名 防止重复
                InputStream inputStream = new FileInputStream(QRCodeImgFile);
                if (FtpUtil.uploadFile(ftpIp, 21, "cls", "123", upPath, filename, inputStream)) {
                    urMap.put("uri", filename);
                    urMap.put("url", ftpPropertiesUtil.getValue("ftp.server.http.prefix")+upPath+ filename);
                }
                payPreCreateVo.setQr_code(urMap.get("url"));  // 将二维码在ftp中的url返回
                return ServerResponse.createSuccessMsgData("支付宝预下单成功: )",payPreCreateVo);
//                System.out.println("支付宝预下单成功: ) "+" body: "+preCreateResponse.getBody());

            case FAILED:
                return ServerResponse.createErrorMessage("支付宝预下单失败!!!");
//                System.out.println("支付宝预下单失败!!!");

            case UNKNOWN:
                return ServerResponse.createErrorMessage("系统异常，预下单状态未知!!!");
//                System.out.println("系统异常，预下单状态未知!!!");

            default:
                return ServerResponse.createErrorMessage("不支持的交易状态，交易返回异常!!!");
//                System.out.println("不支持的交易状态，交易返回异常!!!");
        }

    }

    public PayPreCreateVo setPayPreCreateVo(String Body){
//        String Body="{\"alipay_trade_precreate_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"out_trade_no\":\"tradeprecreate15548786187562775965\",\"qr_code\":\"https:\\/\\/qr.alipay.com\\/bax06600csf27wfwmrmz0020\"},\"sign\":\"Dg5z4B9dCQA/+Vh8AfzC4hBFGE0Ufhvog3VpkKTUiFeMrJyq++OspFJoszNC/y10ukE+hCYxH34fNM54DK1eUAXF8HFB/5G1eblU1U8gH0IstBvK4muqPpbRgqT7nnh6LDK/pXEC56/j/ZZfEzFB6uaj5bgtQBhw/ONaoiwtJ5pDhWLSMhKL24/3cRIYaMudSK7OH4URJwKvOj5nK5dBVhHKE9mAdCUScfvCm88C53LwVDjPEhOFEBH+680Hmsnzt4aMp1NMC539ScsPY3NrgWvqAxX0oU3bKEHLT6W/PJgwz0WcrhFF7qBgO4T+jkDF9S3VY1SnprBzoG/8YEzCqw==\"}";
//        Map<String,String> bodyMap=new HashMap<>();
        PayPreCreateVo payPreCreateVo=new PayPreCreateVo();
        String regex1="\"out_trade_no\":\"(.*?)\",";
        String regex2="\"qr_code\":\"(.*?)\"},";
        String regex3="\"sign\":\"(.*?)\"}";
        Pattern pattern=Pattern.compile(regex1);
        Matcher m=pattern.matcher(Body);
        while (m.find()){
//            System.out.println(m.group(1));
//            bodyMap.put("out_trade_no",m.group(1));
            payPreCreateVo.setOut_trade_no(m.group(1));
            break;
        }
        m=Pattern.compile(regex2).matcher(Body);
        while (m.find()){
//            System.out.println(m.group(1));
//            bodyMap.put("qr_code",m.group(1));
            payPreCreateVo.setQr_code(m.group(1));
            break;
        }
        m=Pattern.compile(regex3).matcher(Body);
        while (m.find()){
//            System.out.println(m.group(1));
//            bodyMap.put("sign",m.group(1));
            payPreCreateVo.setSign(m.group(1));
            break;
        }
        return payPreCreateVo;
    }


    /**
     * @Description:  查询订单状态 (应该在指定时间内轮询，3~5s查询一次)
     * @param
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse queryOrderStatus(long orderNo){
//        String orderNo="1491753014256";
        AlipayF2FQueryResult result=PayAction.trade_query(String.valueOf(orderNo));
        switch (result.getTradeStatus()){
            case SUCCESS:
                AlipayTradeQueryResponse response = result.getResponse();
                return ServerResponse.createSuccessMsg("查询返回该订单支付成功: )");
//                System.out.println("查询返回该订单支付成功: )");
//                System.out.println(response);

            case FAILED:
                return ServerResponse.createErrorMessage("查询返回该订单支付失败或被关闭!!!");
//                System.out.println("查询返回该订单支付失败或被关闭!!!");

            case UNKNOWN:
                return ServerResponse.createErrorMessage("系统异常，订单支付状态未知!!!");
//                System.out.println("系统异常，订单支付状态未知!!!");

            default:
                return ServerResponse.createErrorMessage("不支持的交易状态，交易返回异常!!!");
//                System.out.println("不支持的交易状态，交易返回异常!!!");
        }

    }

    /**
     * @Description:  处理支付宝回调信息
     * @param Params
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse alipayCallback(Map<String,String> Params){
        String orderNo=Params.get("out_trade_no");  // 商品订单编号
        String tradeStatus=Params.get("trade_status");
        Order order=orderMapper.selectByOrderNo(Long.parseLong(orderNo));  // 根据订单编号查询订单
        if(order==null){
            System.out.println("找不到订单，非mmall商场的订单,验证不正确");
            return ServerResponse.createErrorMessage("找不到订单，非mmall商场的订单,验证不正确");
        }
        if(order.getPayment().doubleValue()!=Double.valueOf(Params.get("total_amount"))){
            System.out.println("交易金额不正确,验证不正确");
            return ServerResponse.createErrorMessage("交易金额不正确,验证不正确");
        }
        if(!Params.get("seller_id").equals(Configs.getPid())){
            System.out.println("seller_id不正确,验证不正确");
            return ServerResponse.createErrorMessage("seller_id不正确,验证不正确");
        }
        // 验证通过之后

        if(order.getStatus()>=Const.orderStatusEnum.alreadyPay.getCode()){
            System.out.println("已经接收到支付宝回调状态：TRADE_SUCCESS，之后的回调信息就不做处理了");
            return ServerResponse.createSuccessMsg("已经接收到支付宝回调状态：TRADE_SUCCESS，之后的回调信息就不做处理了");
        }
        if(tradeStatus.equals(Const.alipayCallBack.tradeStatusTRADE_SUCCESS)){ // 支付成功
            order.setStatus(Const.orderStatusEnum.alreadyPay.getCode());
            try {
                order.setPaymentTime(dateConvertUtil.stringToDate(Params.get("gmt_payment"),"yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            orderMapper.updateByPrimaryKeySelective(order);  // 更新
//            return ServerResponse.createSuccessMsg("支付成功");
        }

        // 在TRADE_SUCCESS之前 每一次回调 记录一次payInfo（就两种状态：WAIT_BUYER_PAY、TRADE_SUCCESS）
        PayInfo payInfo=new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(Long.valueOf(orderNo));
        payInfo.setPayPlatform(Const.pay_platformEnum.alipay.getCode());  // 支付平台
        payInfo.setPlatformNumber(Params.get("trade_no"));  // 支付宝交易号
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        System.out.println("支付宝回调success，trade_status："+tradeStatus);
        return ServerResponse.createSuccessMsg("success");
    }

    @Override
    public ServerResponse queryOrderPay(long orderNo,int userId){
        Order order=orderMapper.selectByOrderNoAndUserId(orderNo,userId);
        if(order==null){
            return ServerResponse.createErrorMessage("该用户没有这个订单");
        }
        if(order.getStatus()>=Const.orderStatusEnum.alreadyPay.getCode()){
            return ServerResponse.createSuccessMsg("已经支付成功");
        }
        return ServerResponse.createErrorMessage("还未支付");

    }


}
