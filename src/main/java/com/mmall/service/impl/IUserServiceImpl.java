package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.mmall.util.MD5Util.MD5EncodeUtf8;

/**
 * @description:
 * @author: cls
 * @create: 2019-02-22 10:53
 **/
@Service("iUserService")
public class IUserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        User user=userMapper.selectBaseUserByUsername(username);
        if(user==null){
            return ServerResponse.createErrorMessage("用户名不存在");
        }
        String MD5password=MD5EncodeUtf8(password);  // 数据库中密码是MD5加密了的 所以也要将输入密码加密 才能比较
        if(!user.getPassword().equals(MD5password)){
            return ServerResponse.createErrorMessage("密码错误");
        }
        // 将密码置空 （保密作用）
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createSuccessMsgData("登录成功",user);
    }

    @Override
    public ServerResponse register(User user) {
        int cout;
        if(user.getUsername()==null){    // 其实是多余的判断  判断注册信息是否为空，应该放在前端处理
            return ServerResponse.createErrorMessage("用户名不能为空");
        }
        String username=userMapper.checkName(user.getUsername()); // 在数据库中查找注册用户名
        if(user.getUsername().equals(username)){
            return ServerResponse.createErrorMessage("用户名已存在");
        }
        user.setRole(Const.Role_Customer);  // 设置权限为普通用户
        user.setPassword(MD5EncodeUtf8(user.getPassword()));  // 密码 MD5加密
        cout=userMapper.insert(user);  // 将user插入数据库
        if(cout==0){
            return ServerResponse.createSuccessMsg("插入数据库异常，注册失败");
        }
        return ServerResponse.createSuccessMsg("注册成功");
    }

    /**
     * @Description:  验证用户名是否存在
     * @param type  要验证的字段类型
     * @param str
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse checkValid(String type,String str){
        if(Const.Type_Username.equals(type)){  // 如果要验证的是 username
            String username=userMapper.checkName(str); // 在数据库中查找注册用户名
            if(str.equals(username)){
                return ServerResponse.createErrorMessage("用户名已存在");
            }else {
                return ServerResponse.createSuccessMsg("新注册的用户名有效");
            }
        }
        return ServerResponse.createSuccessMsg("需要验证的字段错误");
    }

    /**
     * @Description: 忘记密码后 返回提示问题(先判断用户名是否存在)
     * @param username
     * @return: com.mmall.common.ServerResponse<java.lang.String>
     */
    @Override
    public ServerResponse<String> forgetPassword(String username){
        if(userMapper.checkName(username)==null){
            return ServerResponse.createErrorMessage("用户名不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createSuccessMsgData("提示问题：",question);
        }
        return ServerResponse.createErrorMessage("找回密码的答案是空的");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){   // 查询到结果，说明答案正确
            String forgetToken=UUID.randomUUID().toString();  // 产生一个随机的token
            RedisShardedPoolUtil.setEx(Const.redis.token_prefix+username,forgetToken,Const.redis.redisSessionTime); // 将token放入本地缓存
//            TokenCache.setKey(TokenCache.token_prefix+username,forgetToken);  // 将token放入本地缓存
            // 将token发送给客户端
            return ServerResponse.createSuccessMsgData("忘记密码的答案正确",forgetToken);
        }
        return ServerResponse.createErrorMessage("答案错误");
    }

    /**
     * @Description:  忘记密码，回答问题正确则重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return: com.mmall.common.ServerResponse
     */
    @Override
    public ServerResponse forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(userMapper.checkName(username)==null){
            return ServerResponse.createErrorMessage("用户名不存在");
        }
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createErrorMessage("forgetToken不能为空");
        }
//        String localToken=TokenCache.getValue(TokenCache.token_prefix+username);
        String localToken= RedisShardedPoolUtil.get(TokenCache.token_prefix+username);  // 从redis缓存中取
        if(StringUtils.isBlank(localToken)){
            return ServerResponse.createErrorMessage("服务器端的token无效或过期");
        }
        if(StringUtils.equals(forgetToken,localToken)){
            int resultCow;
            String passwordMD5=MD5Util.MD5EncodeUtf8(passwordNew);  // 新密码MD5加密
            resultCow=userMapper.forgetChangePassword(username,passwordMD5);
            if(resultCow>0){
                return ServerResponse.createSuccessMsg("密码修改成功");
            }else {
                return ServerResponse.createErrorMessage("数据库插入异常，修改密码失败");
            }
        }
        return ServerResponse.createErrorMessage("token错误");
    }

    /***
     * @Description:  登陆状态下，修改密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return: com.mmall.common.ServerResponse
     */
    public ServerResponse resetPassword(String passwordOld,String passwordNew,User user){
        String MD5passwordOld=MD5EncodeUtf8(passwordOld);
        int resultCount=userMapper.checkOldPassword(MD5passwordOld,user.getId());
        if(resultCount==0){
            return ServerResponse.createErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount=userMapper.updateByPrimaryKeySelective(user);
        if(resultCount>0){
            return ServerResponse.createSuccessMsg("密码修改成功");
        }
        return ServerResponse.createErrorMessage("数据库插入异常，密码修改失败");
    }

    /**
     * @Description:  更新用户信息 （email唯一性）
     * @param user
     * @return: com.mmall.common.ServerResponse
     */
    public ServerResponse<User> updateUserInfo(User user){
        int resultCount=userMapper.checkEmailById(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createErrorMessage("email已存在");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        // 只允许修改以下信息
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());

        resultCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount>0){    // 更新成功
            User newUser=userMapper.selectByPrimaryKey(updateUser.getId());  // 获取更新后的用户信息
            user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);  // 密码置空
            return ServerResponse.createSuccessMsgData("用户信息更新成功",newUser);
        }
        return ServerResponse.createErrorMessage("数据库插入异常，更新失败");
    }



    // backend
    public ServerResponse checkAdminRole(User user){
        if(user==null){
            return ServerResponse.createNeedLoginMessage("请先登录");
        }
        int role=user.getRole();
        if(role== Const.Role_Admin) {   // 是管理员
            return ServerResponse.createSuccess();
        }
        return ServerResponse.createErrorMessage("该用户不是管理员，无权限操作");
    }

}
