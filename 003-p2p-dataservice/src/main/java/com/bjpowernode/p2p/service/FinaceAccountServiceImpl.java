package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.FinaceAccountMapper;
import com.bjpowernode.p2p.model.FinaceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账户业务实现类
 */
@Service(interfaceClass =FinaceAccountService.class,version = "1.0.0",timeout = 20000)
@Component
public class FinaceAccountServiceImpl implements FinaceAccountService {
    @Autowired
    FinaceAccountMapper finaceAccountMapper;

    //根据用户编号查询账户信息
    @Override
    public FinaceAccount queryFinaceAccountByUserId(Integer uid) {
        return   finaceAccountMapper.selectFinaceAccountByUserId(uid);

    }
}
