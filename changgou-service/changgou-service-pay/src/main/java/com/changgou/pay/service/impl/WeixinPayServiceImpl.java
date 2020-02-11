package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.pay.service.impl
 * @version 1.0
 * @date 2020/1/14
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    /**
     * {out_trade_no:12321,totaol_fee:1,from:1,username:zhangsan}
     * @param parameters
     * @return
     */
    @Override
    public Map createNative(Map<String,String> parameters) {
        try {
            //1.组装参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("body", "畅购");
            paramMap.put("out_trade_no", parameters.get("out_trade_no"));
            paramMap.put("total_fee", parameters.get("total_fee"));
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", notifyurl);
            paramMap.put("attach", JSON.toJSONString(parameters));//附件参数
            paramMap.put("trade_type", "NATIVE");//扫码支付
            // todo  签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);//自动添加签名
            //2.需要使用到 httpclient 模拟浏览器发送请求（sdk） 调用统一下单的API
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();//发送请求动作执行
            String content = httpClient.getContent();//模拟浏览器接收 微信的支付系统返回的响应结果
            System.out.println(content);
            //4.获取到微信支付系统返回的数据（code_url）
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("out_trade_no", parameters.get("out_trade_no"));
            resultMap.put("total_fee", parameters.get("total_fee"));
            resultMap.put("code_url", stringStringMap.get("code_url"));


            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }

    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            //1.组装参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", out_trade_no);

            // todo  签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);//自动添加签名
            //2.需要使用到 httpclient 模拟浏览器发送请求（sdk） 调用统一下单的API
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();//发送请求动作执行
            String content = httpClient.getContent();//模拟浏览器接收 微信的支付系统返回的响应结果
            System.out.println(content);
            //4.获取到微信支付系统返回的数据（code_url）
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }
}
