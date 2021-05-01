package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.bjpowernode.p2p.Constant.Constants;
import com.bjpowernode.p2p.alipay.config.AlipayConfig;
import com.bjpowernode.p2p.model.FinaceAccount;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.RechargeRecordService;
import com.bjpowernode.p2p.utils.HttpClientUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.out;

/**
 * 充值的控制层
 */
@Controller
public class RechargeController {
    @Reference(interfaceClass =RechargeRecordService.class,version = "1.0.0",timeout = 20000)
    RechargeRecordService rechargeRecordService;

    //跳转充值页面
    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge(){
        out.println("-----recharge-----");
        return "toRecharge";
    }


    //支付宝充值
    @RequestMapping("/loan/toRecharge/alipay")
    public String alipay(@RequestParam(name="rechargeMoney",required = true)Double rechargeMoney,
                         HttpServletRequest request, Model model){
        out.println("----alipay---"+rechargeMoney);
        //验证金额
        if(rechargeMoney<=0d){
            model.addAttribute("trade_msg","充值失败");
            return "toRecharge";
        }
        //验证用户是否登录
        User user = (User)request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return "login";
        }

        if(StringUtils.equals("",user.getName())||!ObjectUtils.allNotNull(user.getName())){
            return "realName";
        }

        //充值下单
        RechargeRecord rechargeRecord=new RechargeRecord();
        rechargeRecord.setRechargeDesc("支付宝充值");
        rechargeRecord.setRechargeMoney(rechargeMoney);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //课后：同一个账户，多地登录，同时下单，订单重复问题
        String rechargeNo=dateFormat.format(new Date())+user.getId()+ com.bjpowernode.p2p.utils.StringUtils.generateCode(6);
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setUid(user.getId());

        Integer num= null;
        try {
            num = rechargeRecordService.recharge(rechargeRecord);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg","订单生成失败");
            return "toRecharge";
        }

        if(num!=1){
            model.addAttribute("trade_msg","订单生成失败");
            return "toRecharge";
        }

        //支付：调用pay工程
        //HttpClientUtils.doPost()

        //return "redirect:http://localhost:8007/007-p2p-pay//loan/Pay/aliPay?rechargeNo=11111110000&rechargeMoney=78945600";
        model.addAttribute("rechargeRecord",rechargeRecord);
        return "toAlipay";
    }


    @RequestMapping("/loan/toRecharge/payBack")
    public String payBack(HttpServletRequest request,Model model) throws UnsupportedEncodingException, AlipayApiException {
        out.println("-------payBack------");

        //获取支付宝GET过来反馈信息  k-v  name-value
        Map<String,String> params = new HashMap<String,String>();

        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";//rechargeNo:11110000,rechargeMoney:567789,subject:zhifubaozhifu,hobby:sing,dance,code
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //调用SDK验证签名:验证数据是否安全
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {

            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");


            //查询订单交易情况
            Map<String,Object> parasMap=new HashMap<String,Object>();
            parasMap.put("out_trade_no",out_trade_no);

            try {
                String result = HttpClientUtils.doPost("http://localhost:8007/007-p2p-pay/loan/Pay/aliPayQuery", parasMap);
                /**
                 * {
                 * 	"alipay_trade_query_response": {
                 * 		"code": "10000",
                 * 		"msg": "Success",
                 * 		"buyer_logon_id": "gxf***@sandbox.com",
                 * 		"buyer_pay_amount": "0.00",
                 * 		"buyer_user_id": "2088622955586503",
                 * 		"buyer_user_type": "PRIVATE",
                 * 		"invoice_amount": "0.00",
                 * 		"out_trade_no": "2021032514322633172416",
                 * 		"point_amount": "0.00",
                 * 		"receipt_amount": "0.00",
                 * 		"send_pay_date": "2021-03-25 14:32:41",
                 * 		"total_amount": "1000.00",
                 * 		"trade_no": "2021032522001486500501408951",
                 * 		"trade_status": "TRADE_SUCCESS"
                 *        },
                 * 	"sign": "cRQKy4Tvveyj4/4Nbm1TeKxGNGjEvUTBZtqSXB8G6R9FhYbHGHCzym8elCAfkSYege9DGmsSPZf9dicoZn+Y79fG9Bl1QeHLGM7+Pboo5C+o+ixS2dhgP/AGv1L5myTiyHr/KzrTpayzyBk4lfY7hmTVKV9BgPuiRm4rHlxLAR4QIrJ4eSLuBW5olCfPl59a50rLpOCwPizT+Gza2jkP1Ac2QdlDMi6PHIfsJPzaVdVymbQ07dDC5iWGofoUQ6yJMt10Jb71l2SsY9DqkPFO9fTtMr/uboDqyOG7idlIQT+2eF60Xe4nZ1Vi3sTDM2wwOx0OJfgRqf0CoAcAlHTDfw=="
                 * }
                 */
                System.out.println(result);


                JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
                String code = jsonObject.getString("code");
                if(!StringUtils.equals("10000",code)){
                    model.addAttribute("trade_msg","支付宝繁忙,请稍后再试，如果有疑问请联系客服~");
                    return "toRechargeBack";
                }
                String trade_status = jsonObject.getString("trade_status");
                if(StringUtils.equals("TRADE_CLOSED",trade_status)){

                    //充值失败：2
                    Integer num=rechargeRecordService.megerRechargeRecordStatusByRechargeNo(out_trade_no);
                    if(num!=1){
                        //订单日志，需要手动处理
                    }
                    model.addAttribute("trade_msg","充值失败");
                    return "toRechargeBack";
                }

                if(StringUtils.equals("TRADE_SUCCESS",trade_status)){

                    //充值成功：1,修改账户余额
                    User user =(User) request.getSession().getAttribute(Constants.LOGIN_USER);
                    if(!ObjectUtils.allNotNull(user)){
                        model.addAttribute("trade_msg","账户余额更新失败，详细信息咨询客服..");
                        return "toRechargeBack";
                    }
                    Map<String,Object> paraMap=new HashMap<String,Object>();
                    paraMap.put("uId",user.getId());
                    paraMap.put("out_trade_no",out_trade_no);
                    //课后：如果状态为1 已经充值成功，返回小金库  乐观锁 同步代码块
                    Integer num=rechargeRecordService.finishRecharRecord(paraMap);
                    if(num!=1){
                        model.addAttribute("trade_msg","账户余额更新失败，详细信息咨询客服..");

                        return "toRechargeBack";//充值反馈页面
                    }


                    //同步session中账户余额
                    FinaceAccount finaceAccount=(FinaceAccount) request.getSession().getAttribute("finaceAccount");
                    finaceAccount.setAvailableMoney(finaceAccount.getAvailableMoney()+Double.parseDouble(total_amount));

                    return "redirect:/loan/page/myCenter";
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }else {
            System.out.println("验签失败");
        }


        return "";
    }

    //微信充值
    @RequestMapping("/loan/toRecharge/wxpay")
    public String wxpay(@RequestParam(name="rechargeMoney",required = true)Double rechargeMoney,Model model,
                        HttpServletRequest request){
        System.out.println("----wxpay---"+rechargeMoney);
        //验证金额
        if(rechargeMoney<=0d){
            model.addAttribute("trade_msg","充值失败");
            return "toRecharge";
        }
        //验证用户是否登录
        User user = (User)request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return "login";
        }

        if(StringUtils.equals("",user.getName())||!ObjectUtils.allNotNull(user.getName())){
            return "realName";
        }

        //充值下单
        RechargeRecord rechargeRecord=new RechargeRecord();
        rechargeRecord.setRechargeDesc("微信充值");
        rechargeRecord.setRechargeMoney(rechargeMoney);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //课后：同一个账户，多地登录，同时下单，订单重复问题
        String rechargeNo=dateFormat.format(new Date())+user.getId()+ com.bjpowernode.p2p.utils.StringUtils.generateCode(6);
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setUid(user.getId());

        Integer num= null;
        try {
            num = rechargeRecordService.recharge(rechargeRecord);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg","订单生成失败");
            return "toRechargeBack";
        }

        if(num!=1){
            model.addAttribute("trade_msg","订单生成失败");
            return "toRechargeBack";
        }


        model.addAttribute("rechargeRecord",rechargeRecord);

        return "wxPay";
    }


    @RequestMapping("/loan/toRecharge/generateQRCode")
    public void generateQRCode(Model model, HttpServletResponse response,@RequestParam(name="rechargeNo",required = true)String rechargeNo){

        //创建一个矩阵对象
        try {

            //请求-pay统一下单接口-微信统一下单api

//            if(!StringUtils.equals("",rechargeNo)){
//                rechargeRecordService.queryRechargeRecordByRechargeNo();
//            }
//
//            Map<String, Object> paraMap=new HashMap<String, Object>();
//
//            paraMap.put("out_trade_no",out_trade_no);
//            paraMap.put("total_fee",total_fee);
//            paraMap.put("body","微信支付");
//             HttpClientUtils.doPost("http://localhost:8007/007-p2p-pay/loan/Pay/wxPay",map);

            String result="{\n" +
                    "\t\"nonce_str\": \"M3YFRv8FAAnFUOUH\",\n" +
                    "\t\"code_url\": \"weixin://wxpay/bizpayurl?pr=PMtjzkezz\",\n" +
                    "\t\"appid\": \"wx8a3fcf509313fd74\",\n" +
                    "\t\"sign\": \"39111AB90E0AC9B512FA6B818460CDE1\",\n" +
                    "\t\"trade_type\": \"NATIVE\",\n" +
                    "\t\"return_msg\": \"OK\",\n" +
                    "\t\"result_code\": \"SUCCESS\",\n" +
                    "\t\"mch_id\": \"1361137902\",\n" +
                    "\t\"return_code\": \"SUCCESS\",\n" +
                    "\t\"prepay_id\": \"wx29100929813866c59fba15a1ca1cb60000\"\n" +
                    "}";

            JSONObject jsonObject = JSONObject.parseObject(result);
            String codeUrl=jsonObject.getString("code_url");
            Map<EncodeHintType,Object> map= new HashMap<EncodeHintType, Object>();
            map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE, 200, 200, map);
            MatrixToImageWriter.writeToStream(bitMatrix,"jpg",response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //


    }

}
