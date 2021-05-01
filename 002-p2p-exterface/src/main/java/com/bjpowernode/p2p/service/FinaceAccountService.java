package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.FinaceAccount;

import java.util.Map;

/**
 * 账户业务接口
 */
public interface FinaceAccountService {
    /**
     * 根据用户编号查询账户信息
     * @param
     * @return
     */
    FinaceAccount queryFinaceAccountByUserId(Integer uid);



}
