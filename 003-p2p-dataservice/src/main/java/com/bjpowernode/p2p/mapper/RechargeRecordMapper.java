package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.RechargeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    //充值：根据订单编号查询订单信息
    RechargeRecord selectRechargeRecordByRecharNo(Map<String, Object> paraMap);

    //充值失败，修改订单状态为2
    Integer updateRechargeRecordStatusByRechargeNo(String out_trade_no);

    //定时器：查询状态为0的订单
    List<RechargeRecord> selectRechargeRecordByZeroStatus();


}