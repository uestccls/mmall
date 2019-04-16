package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.service.IFileFTPService;
import com.mmall.util.FtpUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @description: ftp上传文件服务类
 * @author: cls
 **/
@Service("iFileFTPService")
public class IFileFTPServiceImp implements IFileFTPService {

    /**
     * @Description:  上传文件到ftp服务器
     * @param file
     * @return: com.mmall.common.ServerResponse<java.util.Map>
     *     返回文件访问的url（访问ftp服务器文件的ip，通过nginx转发 指向ftp服务器的存储根目录）
     */
    @Override
    public ServerResponse<Map> uploadFile(MultipartFile file) throws IOException {
        if(!file.isEmpty()) {
            Map urMap = new HashMap();
            // 读取ftp配置信息
            InputStream in = IFileFTPServiceImp.class.getClassLoader().getResourceAsStream("ftp.properties");
            Properties p = new Properties();
            p.load(in);
            String ftpIp = p.getProperty("ftp.ftpServerIp");
            int ftpPort = Integer.parseInt(p.getProperty("ftp.ftpServerPort"));
            String ftpUsername = p.getProperty("ftp.ftpUsername");
            String ftpPassword = p.getProperty("ftp.ftpPassword");
//            System.out.println(ftpIp + " " + ftpPort + " " + ftpUsername + " " + ftpPassword);

            String path = "images/";  // 设置上传路径
            String filename = file.getOriginalFilename();  //上传文件名
            String suffix = filename.substring(filename.lastIndexOf(".")); // 获取文件名后缀
            filename = UUID.randomUUID().toString() + suffix;  // 重置文件名 防止重复
            InputStream inputStream = file.getInputStream();

            // 上传到ftp服务器
            if (FtpUtil.uploadFile(ftpIp, 21, "cls", "123", path, filename, inputStream)) {
                urMap.put("uri", filename);
                urMap.put("url", p.getProperty("ftp.server.http.prefix")+path+ filename);
                return ServerResponse.createSuccessMsgData("上传图片到ftp服务器成功", urMap);
            }
            return ServerResponse.createErrorMessage("上传图片到ftp服务器失败");
        }

        return ServerResponse.createErrorMessage("失败，没有检测到上传的文件");
    }



}
