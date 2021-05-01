package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //查询注册总人数
    Long selectAllUserCount();

    //根据手机号码查询用户
    User selectUserByPhone(String phone);
    //登录
    User selectUserByPhoneAndPasswd(String phone, String loginPassword);
}