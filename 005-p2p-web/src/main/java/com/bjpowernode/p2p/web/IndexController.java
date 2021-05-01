package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.Constant.Constants;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制层
 */
@Controller
public class IndexController {
    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 20000)
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = UserService.class,version = "1.0.0",timeout = 20000)
    UserService userService;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;

    @RequestMapping("/index")
    public  String index(Model model){
        System.out.println("-------index----");

        //1、历史年化收益率
        Double historyAvgRate=loanInfoService.queryHistoryAvgRate();
        model.addAttribute(Constants.HISTORY_AVG_RATE,historyAvgRate);

        //2、平台总人数
        Long allUserCount= userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);

        //3、累计成交额
        Double bidMoneySum=bidInfoService.queryBidMoneySum();
        model.addAttribute(Constants.BID_MONEY_SUM,bidMoneySum);

        /**
         *变：类型  个数 =》参数
         * 不变： 同一张表
         *
         *   select * from b_loan_info b where b.product_type=2 limit 2,5
         */
        Map<String,Object> parasMap=new HashMap<String,Object>();



        //新手宝
        parasMap.put("loanType",0);
        parasMap.put("start",0);
        parasMap.put("content",1);

        List<LoanInfo> loanInfos= loanInfoService.queryLoanInfoByTypeAndCount(parasMap);
        model.addAttribute(Constants.LOAN_INFO_X,loanInfos);
        //优选标

        parasMap.put("loanType",1);
        parasMap.put("start",0);
        parasMap.put("content",4);

         loanInfos= loanInfoService.queryLoanInfoByTypeAndCount(parasMap);
        model.addAttribute(Constants.LOAN_INFO_Y,loanInfos);
        //散标
        parasMap.put("loanType",2);
        parasMap.put("start",0);
        parasMap.put("content",8);

         loanInfos= loanInfoService.queryLoanInfoByTypeAndCount(parasMap);
        model.addAttribute(Constants.LOAN_INFO_S,loanInfos);
        return "index";


    }

}
