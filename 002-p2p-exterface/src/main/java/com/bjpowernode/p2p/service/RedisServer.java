package com.bjpowernode.p2p.service;

/**
 * redis服务接口
 */
public interface RedisServer {
    /**
     * 验证码：把验证码存入redis中
     * @param phone
     * @param code
     */
    void push(String phone, String code);

    /**
     * 验证码:根据电话号码获取验证码
     * @param phone
     * @return
     */
    String pop(String phone);
}
