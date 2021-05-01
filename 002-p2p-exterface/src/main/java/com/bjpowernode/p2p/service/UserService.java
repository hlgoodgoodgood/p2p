package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.User;

/**
 * 用户业务接扣
 */
public interface UserService {

    /**
     * 查询注册总人数
     * @return
     */
    Long queryAllUserCount();


    /**
     * 根据手机号码查询用户
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 注册
     */

    User regist(String phone, String loginPassword);

    /**
     * 实名认证
     * @param user
     * @return
     */
    int realName(User user);

    /**
     * 登录
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword);
}
