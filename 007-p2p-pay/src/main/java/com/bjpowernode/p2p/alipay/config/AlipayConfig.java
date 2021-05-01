package com.bjpowernode.p2p.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000117627114";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDPYt0zB/V6038vDMt5T0nVT6wc49yRWN4t77n+VlWFpa9eT6XNH4QsYiA5lT0+626Qo4hjmhvAlwTEpvA4KPuPRqpTJB5vvR0p+ZSuH6rDYqWt3HIsueemMSgyJfvuNmuNJ00DnCLpBL+lXaIyRZcJhllo5NNgQcVVocF6pPwcMhnbAzGiln5Dre7RPBp89j5xYDDLzp0qgz3ioKsRVYAocQ2ucxNntjdrSt1jUSaQTnpz/Y38rfzlNRFMThtq4jCskjnNEG0l1SzQjrnDz9zBiuW4UAEZ8cYKgGNFgX9V8QVbYT0d1xgeOUjoW+RurRYGRwmZYkfuNz7OmOuJn1q5AgMBAAECggEBAJq+NTkvth65E0yDKoxQ+lGdSlqgN2OIwPfbyZeayeOYVntzyM5+trv7V/o/flylhXGNZGaVhiG1TcyCl/n/sn0dCFhTOha8MLeyUBuA64Uw5VkOhW8UJGJgiZKpvwrnNmczrQaFiZL/hRSaAj2qqWFlCu/21jYbvWgA84JU+vz5mW+OQ1U5qdUU1+9NAH58XRtakez4/qlbyN/UgfBagJmqHFdUi3+MHO6PMHcnS86NZMi7ilag2sGU0mHUtzThkWjor8GDd2tUSTGzdWNqRdfMuQcsdiaC8gE7pZngEvqwFtMKAmGnDqMRRfGBHBK0DlhmXqYVQE8v1SPkcIXw4oECgYEA5xtDe8iVs8xFnWiJk8wZ1izt7/15HmOrpVIz8JEgVuFJnhXjvy4fGauu8Vmoe/1LT1K2bRzsZcOqAZe7TRQXyo50NSdSAcFug0oQXominz+Q4tR9PnnFP4VRRmc7Q/Y1g93kxDGbLBrKNbb60z6/mv3jky5tZs9qtAppjdfolukCgYEA5bmD9o9HG6XgKg+7pDbOXnVVQofdel3wh4P79Lcpv8D7FZuqNOyMQD0ihqNPLvr5egLlpVdfCcgtdrpiRYHIPDwPs7ZWe5s0B/ilYiCzDW2xaDRoylOcTqE240Ds9t5+dla8tFUEUZq+VXWjkK7Z5rSKOl0tqtXqaAnRn+0f41ECgYAaC66NUsvcWZc26EMpDwWSZ7nhJDX0QNNlbGBCKnj0katUT689KpuCryCmPq6IWsGUDQRWQ744sdaFG26Wfz83KtrZ6raJUB1+WmiB3w0e2XEgv0XmNp8OS54fSx8Yx7S8SdOwcM7GJ7bGWvrlt9qsQxTUz5Sw04t5AvtmawDhkQKBgH1aEpEKs6F5jO1Gsnwzz74pRkHMg2UPN32q3mQ1Qhm5QwkSbPj+DrXK5jkTidS37EPQquDi4SAkU/KSV6dX3xxHJcZJe71s/iJPYmc+MpXkQkb7OicVIpihTaMJvYQj+lu2jrfUIEwrJSray0rarlo+L6MTTyUGHydwHCupJ5pRAoGBAJTTdANTPLqGsZsW7NakVWadPoK4wPIVyhKPAWRK/HGbcdjcmIVkK/VLx8tkLVJCJ22r3Uhd9CZi+JItvBDQQhQo3xpJfjcAkw2RciDGpFSMIXowTe4MSvNx4k2zEr6rVPfZVTCEkMgkwb6RrMDmL1pZATe0r32imDCecFxoe3gM";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn/PwG+LGQIrzOvBLtMRyVS5ffdZncz4j3raRCIfFlvCH0E+myW/OEiuCBmLUixZwg+zi/H7Af6YpJDqWNWygbercf4r3rsqoGrU0EGb7o2FDjNMriGISZSdeEOJ92Zo4uLOOLLTKMY4G0RSBmhjbN1VRfmKNtkDFFLjFNICeozAfbrPJFuXXv+5apQ9Z1i+ANYO99lfs1/o/zFX1hvkTF6I6wu80po3wptsrOYYn2iLDMrSztAhsr+6NsNrJMEhL28r0aL9C7FSEZdxJ8WJpehKe2gpP3nw9jpy4i1kE1IvBMpAqdZTUFcovKOYSfsQjUFH65YLPshMmRDxaiv+bXwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8102/102_alipay_sdk_war_exploded/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8005/005-p2p-web/loan/toRecharge/payBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

