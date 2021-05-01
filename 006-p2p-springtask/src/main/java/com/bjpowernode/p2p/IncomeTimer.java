package com.bjpowernode.p2p;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.model.IncomeRecord;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.service.IncomeRecordService;
import com.bjpowernode.p2p.service.RechargeRecordService;
import com.bjpowernode.p2p.utils.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sun.net.www.content.text.plain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时器
 */
@Component
public class IncomeTimer {
    @Reference(interfaceClass =IncomeRecordService.class,version = "1.0.0",timeout = 20000)
     IncomeRecordService incomeRecordService;
    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 20000)
    RechargeRecordService rechargeRecordService;



    //生成收益计划
    //@Scheduled(cron = "0/5 * * * * ?")
    public void incomePlan(){
        System.out.println("--收益开始--");
        incomeRecordService.generatePlan();
        System.out.println("--收益结束--");

    }

    //@Scheduled(cron = "0/5 * * * * ?")
    public void incomePlanFan(){
        System.out.println("--收益开始--");
        incomeRecordService.PlanFan();
        System.out.println("--收益结束--");
    }



   @Scheduled(cron = "0/5 * * * * ?")
    public void doRechargeRecord(){
        System.out.println("--收益开始--");
        //1、查询订单状态为0的订单==》List
       List<RechargeRecord> rechargeRecords= rechargeRecordService.queryRechargeRecordByZeroStatus();

        //2、遍历订单，问支付宝
        for(RechargeRecord rechargeRecord:rechargeRecords){
            //查询订单交易情况
            Map<String,Object> parasMap=new HashMap<String,Object>();
            parasMap.put("out_trade_no",rechargeRecord.getRechargeNo());
            try {
                String result = HttpClientUtils.doPost("http://localhost:8007/007-p2p-pay/loan/Pay/aliPayQuery", parasMap);
                JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
                String code = jsonObject.getString("code");
                if(StringUtils.equals("10000",code)){
                    String trade_status = jsonObject.getString("trade_status");
                    if(StringUtils.equals("TRADE_CLOSED",trade_status)){
                        //  TRADE_CLOSED充值失败：2
                        Integer num=rechargeRecordService.megerRechargeRecordStatusByRechargeNo(rechargeRecord.getRechargeNo());
                    }
                    if(StringUtils.equals("TRADE_SUCCESS",trade_status)){
                        // TRADE_SUCCESS充值成功：1,修改账户余额
                        Map<String,Object> paraMap=new HashMap<String,Object>();
                        paraMap.put("uId",rechargeRecord.getUid());
                        paraMap.put("out_trade_no",rechargeRecord.getRechargeNo());
                        //课后：如果状态为1 已经充值成功，返回小金库  乐观锁 同步代码块
                        Integer num=rechargeRecordService.finishRecharRecord(paraMap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("--收益结束--");

    }




    //    @Scheduled(cron = "0/5 * * * * ?")
    //    public void m1(){
    //        System.out.println("-----开始------");
    //        System.out.println("-----结束------");
    //    }

}
