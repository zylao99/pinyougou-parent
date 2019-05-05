package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WXPayService;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.pay.service.impl *
 * @since 1.0
 */
@Service
public class WXPayServiceImpl implements WXPayService {
    public static final String appid = "wx8397f8696b538317";
    //财付通平台的商户账号
    public static final String partner = "1473426802";
    //财付通平台的商户密钥
    public static final String partnerkey = "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";
    //回调URL
    public static final String notifyurl = "http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify";

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            Map paramMap = new HashMap();
            //1.组合参数 组成一个MAP
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            //2.生成随机数（使用SDK来做）
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串
            paramMap.put("body", "品优购");
            paramMap.put("out_trade_no", out_trade_no);
            paramMap.put("total_fee", total_fee);//单位是分
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", notifyurl);
            paramMap.put("trade_type", "NATIVE");
            // //3.生成签名 （使用SKU来做自动的添加签名）
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //4.使用httpclient 模拟浏览器发送https请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");//相当打开浏览器输入地址
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);//实体字符创
            httpClient.post();//通过post请求 按住回车键 发送请求

            //5.使用httpclient 模拟浏览器接收相应
            String resultXmlString = httpClient.getContent();//微信支付系统返回的相应
            System.out.println(resultXmlString);
            //6.取出响应中的二维码连接地址 组合成MAp 返回
            Map<String, String> map = WXPayUtil.xmlToMap(resultXmlString);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("out_trade_no", out_trade_no);
            resultMap.put("code_url", map.get("code_url"));
            resultMap.put("total_fee", total_fee);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }

    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            Map paramMap = new HashMap();
            //1.组合参数 组成一个MAP
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            //2.生成随机数（使用SDK来做）
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机字符串

            paramMap.put("out_trade_no", out_trade_no);



            // //3.生成签名 （使用SKU来做自动的添加签名）
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //4.使用httpclient 模拟浏览器发送https请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");//相当打开浏览器输入地址
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);//实体字符创
            httpClient.post();//通过post请求 按住回车键 发送请求

            //5.使用httpclient 模拟浏览器接收相应
            String resultXmlString = httpClient.getContent();//微信支付系统返回的相应
            System.out.println(resultXmlString);
            //6.取出响应中的二维码连接地址 组合成MAp 返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXmlString);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }
}
