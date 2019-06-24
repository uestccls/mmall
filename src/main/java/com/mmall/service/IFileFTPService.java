package com.mmall.service;

import com.mmall.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: cls
 **/
public interface IFileFTPService {
    ServerResponse<Map> uploadFile(MultipartFile file) throws IOException;

}
