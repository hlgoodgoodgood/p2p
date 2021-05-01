package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.BidInfo;

import java.util.List;
import java.util.Map;

/**
 * 投资业务接口
 */
public interface BidInfoService {
    /**
     * 累计成交额
     * @return
     */
    Double queryBidMoneySum();


    /**
     * 根据产品编号查询投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> queryBidInfoByLoanId(Integer loanId);

    /**
     * 投资
     * @param parasMap
     * @return
     */
    String invest(Map<String, Object> parasMap);
}
