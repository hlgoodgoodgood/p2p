package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis服务实现类
 */
@Service(interfaceClass = RedisServer.class,version = "1.0.0",timeout = 20000)
@Component
public class RedisServerImpl implements RedisServer {
    @Autowired
    RedisTemplate redisTemplate;
    //验证码：把验证码存入redis中
    @Override
    public void push(String phone, String code) {
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
    }

    //验证码:根据电话号码获取验证码
    @Override
    public String pop(String phone) {

        return redisTemplate.opsForValue().get(phone)+"";
    }
}
