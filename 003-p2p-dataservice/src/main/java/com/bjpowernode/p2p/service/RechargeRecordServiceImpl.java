package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.FinaceAccountMapper;
import com.bjpowernode.p2p.mapper.RechargeRecordMapper;
import com.bjpowernode.p2p.model.RechargeRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 充值业务实现类
 */
@Service(interfaceClass =RechargeRecordService.class,version = "1.0.0",timeout = 20000)
@Component
public class RechargeRecordServiceImpl implements RechargeRecordService {
    @Autowired
    RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    FinaceAccountMapper finaceAccountMapper;
    //充值
    @Override
    public Integer recharge(RechargeRecord rechargeRecord) {
        return   rechargeRecordMapper.insertSelective(rechargeRecord);

    }

    //充值：完成充值
    @Override
    @Transactional
    public Integer finishRecharRecord(Map<String, Object> paraMap) {
        RechargeRecord recharRecord=rechargeRecordMapper.selectRechargeRecordByRecharNo(paraMap);
        if(!ObjectUtils.allNotNull(recharRecord)){
            return -1;
        }
        recharRecord.setRechargeStatus("1");
        int num=rechargeRecordMapper.updateByPrimaryKey(recharRecord);
        if(num!=1){
            //事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        paraMap.put("rechargeMoney",recharRecord.getRechargeMoney());
        num= finaceAccountMapper.updateFinaceAccountByRechargeRecord(paraMap);
        if(num!=1){
            //事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return num;

    }

    //充值失败，修改订单状态为2
    @Override
    public Integer megerRechargeRecordStatusByRechargeNo(String out_trade_no) {
        return rechargeRecordMapper.updateRechargeRecordStatusByRechargeNo(out_trade_no);
    }

    //定时器：查询状态为0的订单
    @Override
    public List<RechargeRecord> queryRechargeRecordByZeroStatus() {
        return rechargeRecordMapper.selectRechargeRecordByZeroStatus();

    }
}
