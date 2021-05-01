package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.FinaceAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FinaceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinaceAccount record);

    int insertSelective(FinaceAccount record);

    FinaceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinaceAccount record);

    int updateByPrimaryKey(FinaceAccount record);

    //根据用户编号查询账户信息
    FinaceAccount selectFinaceAccountByUserId(Integer uid);

    //投资：减少余额
    int updateAvailableMoneyByBidMoney(Map<String, Object> parasMap);
    /**
     * 返现:通过收益计划返现到账户
     * @param parasMap
     * @return
     */
    int updateFinaceAccountByIncomeRecord(Map<String, Object> parasMap);

    //充值：根据充值记录更改账户余额
    int updateFinaceAccountByRechargeRecord(Map<String, Object> paraMap);

}