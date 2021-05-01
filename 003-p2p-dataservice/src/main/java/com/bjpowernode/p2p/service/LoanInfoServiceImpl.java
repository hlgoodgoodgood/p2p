package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.LoanInfoMapper;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.model.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 投标产品业务实现类
 */
@Service(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 20000)
@Component
public class LoanInfoServiceImpl implements LoanInfoService {
    @Autowired
    LoanInfoMapper loanInfoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    //查询历史年化收益率
    @Override
    public Double queryHistoryAvgRate() {
        //查询缓存
        Double  historyAvgRate =(Double) redisTemplate.opsForValue().get("historyAvgRate");
        //自己完成：缓存穿透
        if(historyAvgRate==null){
            historyAvgRate= loanInfoMapper.selectHistoryAvgRate();
            redisTemplate.opsForValue().set("historyAvgRate",historyAvgRate,20, TimeUnit.SECONDS);
        }
        return historyAvgRate;
    }

    //首页：根据类型和数量 查询投标产品
    @Override
    public List<LoanInfo> queryLoanInfoByTypeAndCount(Map<String, Object> parasMap) {
        return loanInfoMapper.selectLoanInfoByTypeAndCount(parasMap);
    }

    //根据类型和分页模型查询产品数据
    @Override
    public List<LoanInfo> queryLoanInfoByTypeAndPage(PageModel pageModel, Integer ptype) {
        Map<String,Object> parasMap=new HashMap<String,Object>();
        parasMap.put("loanType",ptype);
        parasMap.put("start",(pageModel.getCunPage()-1)*pageModel.getPageContent());
        parasMap.put("content",pageModel.getPageContent());

        //不可以，因为pageModel并没有返回，所有控制层获得不了数据
       // pageModel.setTotalCount(100l);

        return loanInfoMapper.selectLoanInfoByTypeAndPage(parasMap);

    }

    //根据类型查询产品总记录数
    @Override
    public Long queryLoanInfoCount(Integer ptype) {
        //课后：研究多个参数
        return loanInfoMapper.selectLoanInfoCount(ptype);
    }

    //根据产品编号获取产品信息
    @Override
    public LoanInfo queryLoanInfoByTypeLoanId(Integer loanId) {
       return  loanInfoMapper.selectByPrimaryKey(loanId);
    }

    //投资：根据id查询产品信息
    @Override
    public LoanInfo queryLoanInfoById(Integer loanId) {
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }
}
