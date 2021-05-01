package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.FinaceAccountMapper;
import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.model.FinaceAccount;
import com.bjpowernode.p2p.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务实现类
 */
@Service(interfaceClass = UserService.class,version = "1.0.0",timeout = 20000)
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    FinaceAccountMapper finaceAccountMapper;
    //查询注册总人数
    @Override
    public Long queryAllUserCount() {
        Long allUserCount = (Long)redisTemplate.opsForValue().get("allUserCount");
        //自己完成：缓存穿透
        if(allUserCount==null){
            allUserCount=userMapper.selectAllUserCount();
            redisTemplate.opsForValue().set("allUserCount",allUserCount,20, TimeUnit.MINUTES);
        }
        return allUserCount;
    }

    //根据手机号码查询用户
    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    //注册
    @Override
    @Transactional
    public User regist(String phone, String loginPassword) {
        User user=new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        int num=userMapper.insertSelective(user);
        if(num==1){
            //送大礼包
            FinaceAccount finaceAccount=new FinaceAccount();
            finaceAccount.setUid(user.getId());
            finaceAccount.setAvailableMoney(888d);
            finaceAccountMapper.insertSelective(finaceAccount);
        }
        return user;
    }

    //实名认证
    @Override
    public int realName(User user) {
      return  userMapper.updateByPrimaryKeySelective(user);
    }

    //登录
    @Override
    public User login(String phone, String loginPassword) {
        User user= userMapper.selectUserByPhoneAndPasswd(phone,loginPassword);

        //保证登录行为成功，因为修改最后一次登录时间 不是重要行为
        new Thread(new Runnable() {
            @Override
            public void run() {
                user.setLastLoginTime(new Date());
                userMapper.updateByPrimaryKeySelective(user);
            }
        }).start();

        return user;
    }
}
