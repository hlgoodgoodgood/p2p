package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.Constant.Constants;
import com.bjpowernode.p2p.model.FinaceAccount;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.RedisServer;
import com.bjpowernode.p2p.service.UserService;
import com.bjpowernode.p2p.utils.HttpClientUtils;
import com.bjpowernode.p2p.utils.Result;
import com.bjpowernode.p2p.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制层
 */
@Controller
public class UserController {
    @Reference(interfaceClass = UserService.class,version = "1.0.0",timeout = 20000)
    UserService userService;
    @Reference(interfaceClass = RedisServer.class,version = "1.0.0",timeout = 20000)
    RedisServer redisServer;
    @RequestMapping("/loan/page/register")
    public String regist(){
        System.out.println("---regist--");
        return "register";
    }

    @RequestMapping("/loan/page/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(name="phone",required = true)String phone){

        //根据手机号码查询用户
        User user=userService.queryUserByPhone(phone);
        if(user==null){
            return Result.success();
        }else{
            return Result.error();
        }
    }

    @RequestMapping("/loan/page/registSubmit")
    @ResponseBody
    public Object registSubmit(@RequestParam(name="phone",required = true)String phone,
                               @RequestParam(name="loginPassword",required = true)String loginPassword,
                               @RequestParam(name="messageCode",required = true)String messageCode,
                               HttpServletRequest request){
        //校验：验证码
        String code=redisServer.pop(phone);
        if(!org.apache.commons.lang3.StringUtils.equals(messageCode,code)){
            return Result.error("验证码输入错误:)");
        }

        /**
         *注册：
         * 1、插入一条用户信息
         * 2、送大礼包
         */

       User user= userService.regist(phone,loginPassword);
       //所有对象都不为null 返回true
       if(ObjectUtils.allNotNull(user)){
           //注册成功后，自定登录
           request.getSession().setAttribute(Constants.LOGIN_USER,user);
       }else {
           return Result.error();
       }

        return Result.success();
    }

    @RequestMapping("/loan/page/messageCode")
    @ResponseBody
    public Object messageCode(@RequestParam(name="phone",required = true)String phone){

        //发短信
        String code = StringUtils.generateCode(6);
        String url="https://way.jd.com/kaixintong/kaixintong";

        Map<String,Object> parasMap=new HashMap<String,Object>();

        parasMap.put("appkey","c5afd4c4272f7e2ca6961bb209c95a40");
        parasMap.put("mobile",phone);
        parasMap.put("content","【凯信通】您的验证码是："+code);

        //模拟报文
        String result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-7225293</remainpoint>\\n <taskID>171845880</taskID>\\n <successCounts>1</successCounts></returnsms>\",\n" +
                "    \"requestId\": \"f9f2178421aa4eb5a1f124e42f3ccc69\"\n" +
                "}";

        try {
            //模拟客户端浏览器发送数据
            // result = HttpClientUtils.doPost(url, parasMap);

            //解析响应信息
            JSONObject jsonObject = JSONObject.parseObject(result);
            //获得通信信息
            String code1 = jsonObject.getString("code");
            if(!org.apache.commons.lang3.StringUtils.equals("10000",code1)){
                return Result.error("通信失败");
            }

            //获取xml
            String xml = jsonObject.getString("result");

            //xml解析
            Document document = DocumentHelper.parseText(xml);
            Node node = document.selectSingleNode("//returnstatus");
            String text = node.getText();
            if(!org.apache.commons.lang3.StringUtils.equals("Success",text)){
                return Result.error("发送短信失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统繁忙");

        }
        //把验证码存放到redis中
        redisServer.push(phone,code);
        return Result.success(code);
    }


    @RequestMapping("/loan/page/realName")
    public String realName(){

        return "realName";
    }

    @RequestMapping("/loan/page/realNameSubmit")
    @ResponseBody
    public Object realNameSubmit(@RequestParam(name="phone",required = true)String phone,
                                 @RequestParam(name="realName",required = true)String realName,
                                 @RequestParam(name="idCard",required = true)String idCard,
                                 @RequestParam(name="messageCode",required = true)String messageCode,
                                 HttpServletRequest request){

        User user = (User)request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请登录，再认证");
        }

        //校验：验证码
        String code=redisServer.pop(phone);
        if(!org.apache.commons.lang3.StringUtils.equals(messageCode,code)){
            return Result.error("验证码输入错误:)");
        }


        String url="https://way.jd.com/youhuoBeijing/test";

        Map<String,Object> parasMap=new HashMap<String,Object>();
        parasMap.put("realName",realName);
        parasMap.put("cardNo",idCard);

        parasMap.put("appkey","c5afd4c4272f7e2ca6961bb209c95a40");

        String result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"成功\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"乐天磊\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";
        try {
           // result = HttpClientUtils.doPost(url, parasMap);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String code1 = jsonObject.getString("code");
            if(!org.apache.commons.lang3.StringUtils.equals("10000",code1)){
                return Result.error("通信失败");
            }
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            if(!isok){
                return Result.error("实名认证失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统升级中，请稍后再试~");
        }

        //更新用户信息

        user.setName(realName);
        user.setIdCard(idCard);

        int num=userService.realName(user);
        if(num!=1){
            return Result.error("系统升级中，请稍后再试~");
        }
        return Result.success();
    }

    @RequestMapping("/loan/page/login")
    public String login(@RequestParam(name="ReturnUrl",required = false)String returnUrl, Model model){
        model.addAttribute("returnUrl",returnUrl);
        return "login";
    }
    @RequestMapping("/loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(@RequestParam(name="phone",required = true)String phone,
                              @RequestParam(name="loginPassword",required = true)String loginPassword,
                              HttpServletRequest request){
        /**
         *登录：
         * 1、根据用户名、密码查询用户信息   ==》用户名或密码错误
         * 2、根据用户名查询用户信息，用户信息和密码匹配   ==》精确说明用户名 还是 密码错误 ，容易被破解
         */
        User user=userService.login(phone,loginPassword);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("用户名或密码错误");
        }

        //登录成功后，放入session中
        request.getSession().setAttribute(Constants.LOGIN_USER,user);
        return Result.success();

    }


    @RequestMapping("/loan/page/myCenter")
    public String myCenter(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return "login";
        }
//        //根据用户编号查询账户信息
//        FinaceAccount finaceAccount=finaceAccountService.queryFinaceAccountByUserId(user.getId());
        //账户放入session中
        //request.getSession().setAttribute("finaceAccount",finaceAccount);
        return "myCenter";
    }

    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request){
       //1、session 失效
        request.getSession().invalidate();
        //2、session中移除登录省份
        //request.getSession().removeAttribute(Constants.LOGIN_USER);
        return "redirect:/index";
    }

}
