package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectBaseUserByUsername(String username);

    String checkName(String name);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param(value ="username") String username, @Param(value ="question") String question, @Param(value ="answer") String answer);

    int forgetChangePassword(@Param(value ="username")String username,@Param(value ="passwordNew")String passwordNew);

    int checkOldPassword(@Param(value ="passwordOld")String passwordOld,@Param(value ="UserId")Integer UserId);

    int checkEmailById(@Param(value ="email")String email,@Param(value ="userId")int userId);
}