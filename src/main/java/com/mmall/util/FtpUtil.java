package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description: 文件上传ftp服务器
 * @author: cls
 **/
public class FtpUtil {

    /**
     * @Description:     ftp上传文件
     * @param hostName FTP服务器地址
     * @param port   FTP服务器端口号
     * @param username  FTP登录帐号
     * @param password  FTP登录密码
     * @param path  FTP服务器保存目录  以"/"开头（绝对路径）
     * @param remoteFileName 上传到FTP服务器后的文件名称
     * @param inputStream  输入文件流
     * @return: boolean
     */
    public static boolean uploadFile(String hostName,int port,String username,
          String password,String path,String remoteFileName,InputStream inputStream){
        boolean flag=false;
        FTPClient ftpClient=new FTPClient();
        // 连接服务器
        if(!connectFtpServer(ftpClient,hostName,port,username,password)){
//            System.out.println("ftp连接失败"+flag);
            return flag;
        }
        try {
            //检查上传路径是否存在 如果不存在返回false
            boolean flag1=ftpClient.changeWorkingDirectory(path);
            if(!flag1){
                //创建上传的路径
                flag1=ftpClient.makeDirectory(path);
                System.out.println("makeDirectory结果："+flag1);
            }
            // 不以“/”开头的是相对路径，changeWorkingDirectory 会导致访问ftp 服务器的地址不一样
            flag1=ftpClient.changeWorkingDirectory(path);  //指定上传路径
            System.out.println("changeWorkingDirectory结果："+flag1);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); //指定上传类型为二进制
            // 上传文件 remoteFileName为上传到FTP服务器后的文件名称
            flag=ftpClient.storeFile(remoteFileName,inputStream); // 上传完成
            System.out.println("ftpClient.storeFile结果(上传完成)："+flag);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();     // 关闭文件流
                ftpClient.logout();      // 退出登录
                ftpClient.disconnect();  // 关闭连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("上传  "+flag);
        return flag;
    }

    /**
     * @Description:   连接FTP服务器
     * @param ftpClient
     * @param hostName FTP服务器地址
     * @param port  FTP服务器端口号
     * @param username FTP登录帐号
     * @param password FTP登录密码
     * @return: boolean
     */
    private static boolean connectFtpServer(FTPClient ftpClient,String hostName,int port,String username,String password){
        boolean result=false;
        ftpClient.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器
            ftpClient.connect(hostName,port);
            //登录FTP服务器
            ftpClient.login(username,password);
            //是否成功登录FTP服务器
            int replyCode=ftpClient.getReplyCode();
            if(FTPReply.isPositiveCompletion(replyCode)){
                result=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ftp连接状态："+result);
        return result;
    }




}
