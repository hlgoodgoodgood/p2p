package com.bjpowernode.p2p;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.bjpowernode.p2p.alipay.config.AlipayConfig;
import com.bjpowernode.p2p.utils.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Pay {
    @RequestMapping("/loan/Pay/aliPay")
    public void aliPay(@RequestParam(name = "rechargeNo",required = true)String rechargeNo,
                         @RequestParam(name = "rechargeMoney",required = true)Double rechargeMoney,
                         @RequestParam(name = "subject",required = true)String subject,
                         HttpServletRequest request, HttpServletResponse response) throws AlipayApiException, IOException {

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);



        alipayRequest.setBizContent("{\"out_trade_no\":\""+ rechargeNo +"\","
                + "\"total_amount\":\""+ rechargeMoney +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");


        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //输出
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().println(result);
        System.out.println(result);
       // return "xxx";

    }


    @RequestMapping("/loan/Pay/aliPayQuery")
    @ResponseBody
    public String  aliPayQuery(@RequestParam(name = "out_trade_no",required = true)String out_trade_no) throws AlipayApiException {

        System.out.println("out_trade_no="+out_trade_no);
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();

        //输出
        System.out.println(result);
        return result;
    }


    @RequestMapping("/loan/Pay/wxPay")
    @ResponseBody
    public Object wxPay(@RequestParam(name="out_trade_no")String out_trade_no,
                        @RequestParam(name="total_fee")Double total_fee,
                        @RequestParam(name="body")String body) throws Exception {


//        Map<String, Object> map=new HashMap<String, Object>();
//        map.put("appid","wx8a3fcf509313fd74");
//        map.put("mchid","1361137902");
//        map.put("description",body);
//        map.put("out_trade_no",out_trade_no);
//        map.put("notify_url","http://localhost:8005/005-p2p-web/loan/xxx");
//        map.put("amount",new Amount());
//
//        Map<String, String> paraMap=new HashMap<String, String>();
//
//        paraMap.put("Content-Type","application/json");
//        paraMap.put("Accept","application/json");
//        paraMap.put("User-Agent","xxx");
//        paraMap.put("Authorization","07A74DC40A034557E7F8B3913CD589F5");
//
//        String s = HttpClientUtils.doPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native",paraMap, map);
//        System.out.println(s);
//        return s;


        Map<String, String> map=new HashMap<String, String>();
        map.put("appid","wx8a3fcf509313fd74");
        map.put("mch_id","1361137902");
        //生成随机数，保证数据更安全
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("body", body);
        map.put("out_trade_no",out_trade_no);

        //保证数据不丢失精度  total_fee*100
        BigDecimal bigDecimal=new BigDecimal(total_fee);
        bigDecimal=bigDecimal.multiply(new BigDecimal(100));

        map.put("total_fee", bigDecimal.intValue()+"");
        map.put("spbill_create_ip","127.0.0.1");
        map.put("notify_url","http://localhost:9005/005-p2p-web/loan/xxx");
        map.put("trade_type","NATIVE");
        map.put("product_id",out_trade_no);

        //验签：保证数据的安全
        String signature = WXPayUtil.generateSignature(map, "367151c5fd0d50f1e34a68a802d6bbca");
        map.put("sign",signature);

        System.out.println( WXPayUtil.mapToXml(map));
        String xml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", WXPayUtil.mapToXml(map));
        System.out.println("xml:"+xml);
        return WXPayUtil.xmlToMap(xml);
    }


}


class Amount implements Serializable {
    int  total=100;
}