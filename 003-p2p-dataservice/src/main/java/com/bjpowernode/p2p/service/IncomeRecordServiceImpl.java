package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.BidInfoMapper;
import com.bjpowernode.p2p.mapper.FinaceAccountMapper;
import com.bjpowernode.p2p.mapper.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.IncomeRecord;
import com.bjpowernode.p2p.model.LoanInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收益因为实现类
 */
@Service(interfaceClass =IncomeRecordService.class,version = "1.0.0",timeout = 20000)
@Component
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    LoanInfoMapper loanInfoMapper;
    @Autowired
    BidInfoMapper bidInfoMapper;
    @Autowired
    IncomeRecordMapper incomeRecordMapper;
    @Autowired
    FinaceAccountMapper finaceAccountMapper;


    /**
     * 生成收益计划：
     * 1、查询产品表 获取产品的状态为1 的产品  ==>List
     * 2、遍历产品 ，根据产品id 到投资表 查询该产品的投资记录  ==》List
     * 3、遍历投资记录 ，计算收益时间、收益金额-->收益表
     * 4、修改产品状态 为满标已生成收益计划（2）
     */
    @Override
    public void generatePlan() {
        //1、查询产品表 获取产品的状态为1 的产品  ==>List
        List<LoanInfo> loanInfos=loanInfoMapper.selectLoanInfoByFullStatus();
        //2、遍历产品 ，根据产品id 到投资表 查询该产品的投资记录  ==》List
        for(LoanInfo loanInfo:loanInfos){
           List<BidInfo> bidInfos= bidInfoMapper.selectBidInfosByLoanId(loanInfo.getId());
            //3、遍历投资记录 ，计算收益时间、收益金额-->收益表
           for(BidInfo bidInfo:bidInfos){
               IncomeRecord incomeRecord=new IncomeRecord();
               incomeRecord.setBidId(bidInfo.getId());
               incomeRecord.setBidMoney(bidInfo.getBidMoney());

               Date date=null;
               Double incomeMoney=null;
               if(loanInfo.getProductType()==0){
                   date=DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                   incomeMoney= loanInfo.getRate()/100/365*loanInfo.getCycle()*bidInfo.getBidMoney();
               }else{
                   date=DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                   incomeMoney= loanInfo.getRate()/100/365*loanInfo.getCycle()*30*bidInfo.getBidMoney();

               }

               incomeRecord.setIncomeDate(date);
               incomeRecord.setIncomeMoney(incomeMoney);
               incomeRecord.setIncomeStatus(0);
               incomeRecord.setLoanId(loanInfo.getId());
               incomeRecord.setUid(bidInfo.getUid());
               incomeRecordMapper.insertSelective(incomeRecord);
           }
           //4、修改产品状态 为满标已生成收益计划（2）
            loanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(loanInfo);

        }


    }

    /***
     * 返现：
     * 1、查询当天到期且状态未返回（0）的收益计划==》List
     * 2、获取本金+利息返回用户的账户表
     * 3、修改收益计划状态为已返回（1）
     */

    @Override
    public void PlanFan() {
        //查询当天到期且状态未返回（0）的收益计划==》List
       List<IncomeRecord> incomeRecords= incomeRecordMapper.selectIncomeRecordsByDataAndStatus();
        for(IncomeRecord incomeRecord:incomeRecords){

            Map<String,Object> parasMap=new HashMap<String,Object>();
            parasMap.put("uid", incomeRecord.getUid());
            parasMap.put("bidMoney", incomeRecord.getBidMoney());
            parasMap.put("incomeMoney", incomeRecord.getIncomeMoney());

            //2、获取本金+利息返回用户的账户表
            int nu=finaceAccountMapper.updateFinaceAccountByIncomeRecord(parasMap);

            //3、修改收益计划状态为已返回（1）
            incomeRecord.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);

        }
    }
}
