package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.model.BidInfo;
import com.bjpowernode.p2p.model.LoanInfo;
import com.bjpowernode.p2p.model.PageModel;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.LoanInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class LoanController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 20000)
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 20000)
    BidInfoService bidInfoService;

    @RequestMapping("/loan/loan")
    public String loan(@RequestParam(name="ptype",required = false) Integer ptype,
                       Long cunPage,Model model, HttpServletRequest request){
        System.out.println("---loan--"+ptype);
        /**
         *
         *当前页、每页显示多少个、总记录数、总页数、首页、尾页  =》Map==》类
         * PageModel：cunPage pageContent totalCount  totalPage firstPage lastPage
         */


        //从session获取分页模型
        PageModel pageModel=(PageModel)request.getSession().getAttribute("pageModel");

        //创建分页模型
        if(pageModel==null){
            pageModel=new PageModel();
        }

        //查询总记录数
        Long totalCount=loanInfoService.queryLoanInfoCount(ptype);
        pageModel.setTotalCount(totalCount);

        //为空或者小于第一页，设置为首页
        if(cunPage==null||cunPage<=0){
            cunPage=pageModel.getFirstPage().longValue();
            //课后：原来页面不动
        }

        //超过最后一页，设置为尾页
        if(cunPage>=pageModel.getLastPage()){
            cunPage=pageModel.getLastPage();
            //课后：原来页面不动
        }
        pageModel.setCunPage(cunPage);



        //根据类型和分页模型查询产品数据
        List<LoanInfo> loanInfos=loanInfoService.queryLoanInfoByTypeAndPage(pageModel,ptype);
        model.addAttribute("loanInfos",loanInfos);
        //设置分页模型到session中
        request.getSession().setAttribute("pageModel",pageModel);
        //设置产品类型
        model.addAttribute("ptype",ptype);
        return "loan";
    }


    //下一页：controller（cunPage++）  ，上一页：controller（cunPage--）


    @RequestMapping("/loan/loanInfo")
    public String loanInfo(@RequestParam(name="loanId",required = true) Integer loanId,Model model){

        //获取产品信息
        LoanInfo loanInfo=loanInfoService.queryLoanInfoByTypeLoanId(loanId);
        model.addAttribute("loanInfo",loanInfo);

        //获得投资记录
        List<BidInfo> bidInfos=  bidInfoService.queryBidInfoByLoanId(loanId);
        model.addAttribute("bidInfos",bidInfos);
        return "loanInfo";
    }



}
