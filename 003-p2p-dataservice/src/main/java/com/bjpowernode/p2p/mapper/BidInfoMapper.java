package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.BidInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    //累计成交额
    Double selectBidMoneySum();
    //根据产品编号查询投资记录
    List<BidInfo> selectBidInfoByLoanId(Integer loanId);

    //根据产品编号查询投资记录
    List<BidInfo> selectBidInfosByLoanId(Integer loanId);
}