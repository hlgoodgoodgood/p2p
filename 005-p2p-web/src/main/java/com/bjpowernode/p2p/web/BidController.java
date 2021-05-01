package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.Constant.Constants;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.p2p.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 投资控制层
 */
@Controller
public class BidController {
    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 20000)
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;

    @RequestMapping("/loan/bidInfo/invest")
    @ResponseBody
    public Object invest(@RequestParam(name="loanId",required = true) Integer loanId,
                         @RequestParam(name="bidMoney",required = true) Double bidMoney,
                         HttpServletRequest request){

        //验证：是否登录
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请先登录");
        }
        //验证：实名认证
        if(!ObjectUtils.allNotNull(user.getName())){
            return Result.error("请完成实名认证");
        }

        //验证：金额
        LoanInfo loanInfo=loanInfoService.queryLoanInfoById(loanId);
        if(loanInfo.getBidMaxLimit()<bidMoney){
            return Result.error("您的投资金额超过上限");
        }

        if(loanInfo.getBidMinLimit()>bidMoney){
            return Result.error("您的投资金额没有达到最低限额");
        }

        if(loanInfo.getLeftProductMoney()<=0){
            return Result.error("您的投资产品已经满标");
        }

        if(loanInfo.getLeftProductMoney()<bidMoney){
            return Result.error("您的投资金额超过剩余可投金额");
        }


        /**
         *投资：
         * 1、账户金额减少，判断是否满足投资金额
         * 2、产品剩余可投金额 减少，会不会小于投资金额
         * 3、如果产品剩余可投金额==0，满标（1），满标时间
         * 4、插入投资记录
         */
        Map<String,Object> parasMap=new HashMap<String,Object>();
        parasMap.put("loanId",loanId);
        parasMap.put("bidMoney",bidMoney);
        parasMap.put("uId",user.getId());


        //
//        String code= null;
//        try {
//            code = bidInfoService.invest(parasMap);
//        } catch (LeftMoneyException e) {
//            e.printStackTrace();
//            return Result.error("投资失败");
//        } catch (AvailableMoneyException e) {
//            e.printStackTrace();
//            return Result.error("账户金额不足");
//        }catch (AvailableMoneyException e) {
//            e.printStackTrace();
//            return Result.error("账户金额不足");
//        }
//        try {
//            int n=100/0;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<1000;i++){
                    bidInfoService.invest(parasMap);
                }
            }
        });

//        String code= null;
//        try {
//            code = bidInfoService.invest(parasMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("投资失败");
//        }
//
//        if(StringUtils.equals("502",code)){
//            return Result.error("账户金额不足");
//        }
//        if(StringUtils.equals("503",code)){
//            return Result.error("剩余可投金额不足");
//        }
//
//        if(StringUtils.equals("504",code)){
//            return Result.error("转态更新失败");
//        }
//        if(StringUtils.equals("505",code)){
//            return Result.error("投资失败");
//        }
//        if(StringUtils.equals("200",code)){
//            return Result.success();
//        }
        return null;
    }
}
