package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.Constant.Constants;
import com.bjpowernode.p2p.model.FinaceAccount;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.FinaceAccountService;
import com.bjpowernode.p2p.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 账户控制层
 */
@Controller
public class FinaceAccountController {
    @Reference(interfaceClass =FinaceAccountService.class,version = "1.0.0",timeout = 20000)
    FinaceAccountService finaceAccountService;

    @RequestMapping("/loan/page/queryAccount")
    @ResponseBody
    public  Object queryAccount(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请先登录");
        }
        //根据用户编号查询账户信息
        FinaceAccount finaceAccount=finaceAccountService.queryFinaceAccountByUserId(user.getId());
        //账户放入session中
        request.getSession().setAttribute("finaceAccount",finaceAccount);
        return Result.success(finaceAccount.getAvailableMoney()+"");
    }
}
