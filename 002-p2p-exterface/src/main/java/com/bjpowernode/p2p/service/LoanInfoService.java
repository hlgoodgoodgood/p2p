package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.model.PageModel;

import java.util.List;
import java.util.Map;

/**
 * 投标产品业务接口
 */
public interface LoanInfoService {
    /**
     * 查询历史年化收益率
     * @return
     */
    Double queryHistoryAvgRate();

    /**
     * 首页：根据类型和数量 查询投标产品
     * @param parasMap
     * @return
     */
    List<LoanInfo> queryLoanInfoByTypeAndCount(Map<String, Object> parasMap);

    /**
     * 根据类型和分页模型查询产品数据
      * @param pageModel
     * @param ptype
     * @return
     */
    List<LoanInfo> queryLoanInfoByTypeAndPage(PageModel pageModel, Integer ptype);

    /**
     * 根据类型查询产品总记录数
     * @param ptype
     * @return
     */
    Long queryLoanInfoCount(Integer ptype);

    /**
     * 产品详情：根据产品编号获取产品信息
     * @return
     */
    LoanInfo queryLoanInfoByTypeLoanId(Integer loanId);


    /**
     * 根据id查询产品信息
     * @param loanId
     * @return
     */
    LoanInfo queryLoanInfoById(Integer loanId);
}
