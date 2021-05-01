package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.LoanInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    //查询历史年化收益率
    Double selectHistoryAvgRate();

    //首页：根据类型和数量 查询投标产品
    List<LoanInfo> selectLoanInfoByTypeAndCount(Map<String, Object> parasMap);

    //根据类型和分页模型查询产品数据
    List<LoanInfo> selectLoanInfoByTypeAndPage(Map<String, Object> parasMap);


    //根据类型查询产品总记录数
    Long selectLoanInfoCount(Integer ptype);

    //投资：减少剩余可投金额
    int updateLeftMoneyByBidMoney(Map<String, Object> parasMap);

    //查询满标状态的产品
    List<LoanInfo> selectLoanInfoByFullStatus();


}