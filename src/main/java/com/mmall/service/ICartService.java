package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: cls
 **/
public interface ICartService {
    ServerResponse addToCart(int userId,int productId, int quantity);
    ServerResponse chooseCart(int userId,int productId);


}
