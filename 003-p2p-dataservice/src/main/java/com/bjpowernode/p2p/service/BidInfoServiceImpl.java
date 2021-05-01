package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.BidInfoMapper;
import com.bjpowernode.p2p.mapper.FinaceAccountMapper;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.LoanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 投资业务实现类
 */
@Service(interfaceClass =BidInfoService.class,version = "1.0.0",timeout = 20000)
@Component
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    BidInfoMapper bidInfoMapper;
    @Autowired
    FinaceAccountMapper finaceAccountMapper;
    @Autowired
    LoanInfoMapper loanInfoMapper;

    //累计成交额
    @Override
    public Double queryBidMoneySum() {
        return bidInfoMapper.selectBidMoneySum();
    }

    //根据产品编号查询投资记录
    @Override
    public List<BidInfo> queryBidInfoByLoanId(Integer loanId) {
        return   bidInfoMapper.selectBidInfoByLoanId(loanId);

    }

    /**
     *投资：
     * 1、账户金额减少，判断是否满足投资金额
     * 2、产品剩余可投金额 减少，会不会小于投资金额
     * 3、如果产品剩余可投金额==0，满标（1）
     * 4、插入投资记录
     */
    @Override
    @Transactional
    public String invest(Map<String, Object> parasMap) {

        //1、账户金额减少，判断是否满足投资金额
        int num= finaceAccountMapper.updateAvailableMoneyByBidMoney(parasMap);
        if(num==0){
            //事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "502";
        }

//        synchronized (this){
//            //2、产品剩余可投金额 减少，会不会小于投资金额
//            num=loanInfoMapper.updateLeftMoneyByBidMoney(parasMap);
//            if(num==0){
//                //throw new LeftMoneyException("502");
//                //throw new AvailableMoneyException("502");
//                //throw new RuntimeException("502");
//                //  throw new Exception("xxx");
//
//                //事务手动回滚
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                return "503";
//
//            }
//        }

        //2、产品剩余可投金额 减少，会不会小于投资金额
        LoanInfo loInfo = loanInfoMapper.selectByPrimaryKey(Integer.parseInt(parasMap.get("loanId") + ""));
        //设置版本号(乐观锁)
        parasMap.put("version",loInfo.getVersion());
        num=loanInfoMapper.updateLeftMoneyByBidMoney(parasMap);
        if(num==0){
             //throw new LeftMoneyException("502");
            //throw new AvailableMoneyException("502");
            //throw new RuntimeException("502");
            //  throw new Exception("xxx");

            //事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "503";

        }

        //3、如果产品剩余可投金额==0，满标（1）
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(Integer.parseInt(parasMap.get("loanId") + ""));
        if(loanInfo.getLeftProductMoney()==0d&&loanInfo.getProductStatus()==0){
            loanInfo.setProductStatus(1);
            loanInfo.setProductFullTime(new Date());
            num= loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if(num==0){
                //事务手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "504";
            }
        }

        //4、插入投资记录
        BidInfo bidInfo=new BidInfo();
        bidInfo.setBidMoney(Double.parseDouble(parasMap.get("bidMoney")+""));
        bidInfo.setBidStatus(1);
        bidInfo.setBidTime(new Date());
        bidInfo.setLoanId(Integer.parseInt(parasMap.get("loanId") + ""));
        bidInfo.setUid(Integer.parseInt(parasMap.get("uId") + ""));
        num= bidInfoMapper.insertSelective(bidInfo);
        if(num==0){
            //事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "505";
        }
        return "200";
    }
}
