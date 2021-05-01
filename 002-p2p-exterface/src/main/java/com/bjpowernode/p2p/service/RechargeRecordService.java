package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.RechargeRecord;

import java.util.List;
import java.util.Map;

/**
 * 充值的业务接口
 */
public interface RechargeRecordService {
    /**
     * 充值：充值
     * @param rechargeRecord
     * @return
     */
    Integer recharge(RechargeRecord rechargeRecord);

    /**
     * 充值：完成充值
     * @param paraMap
     * @return
     */
    Integer finishRecharRecord(Map<String, Object> paraMap);

    /**
     * 充值：充值失败，修改订单状态为2
     */
    Integer megerRechargeRecordStatusByRechargeNo(String out_trade_no);

    /**
     * 定时器：查询状态为0的订单
     */
    List<RechargeRecord> queryRechargeRecordByZeroStatus();


}
