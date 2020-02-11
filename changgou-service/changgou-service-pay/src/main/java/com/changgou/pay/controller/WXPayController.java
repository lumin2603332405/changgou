package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.pay.controller
 * @version 1.0
 * @date 2020/1/14
 */
@RestController
@RequestMapping("/weixin/pay")
public class WXPayController {

    @Autowired
    private WeixinPayService payService;


    /**
     * parameters  有from参数  有out_trade_no 有  total_fee 。。。。
     *
     * @param out_trade_no 订单号（交易订单号是同一个）
     * @param total_fee    金额
     * @return
     */
    @RequestMapping("/create/native")
    public Map createNative(@RequestParam Map<String, String> parameters) {


        return payService.createNative(parameters);
    }

    /**
     * 查询某一个支付订单的支付的状态
     *
     * @param out_trade_no 支付订单号（普通订单号）
     * @return
     */
    @RequestMapping("/status/query")
    public Result queryStatus(String out_trade_no) {

        Map<String, String> resultMap = payService.queryStatus(out_trade_no);

        return new Result(true, StatusCode.OK, "查询订单的状态成功", resultMap);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private Environment environment;


    /**
     * 接收微信 通知
     *
     * @return 微信规定的的格式的字符串（XML格式的）
     */
    @RequestMapping("/notify/url")
    public String notifyurl(HttpServletRequest request) {
        String resultxml = null;
        System.out.println("hhhhhhh--------kasjflajsflasjflajflaf");
        //接收数据流
        ServletInputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = request.getInputStream();
            //将数据流转出字节数组
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int leng = 0;
            while ((leng = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, leng);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();//所的数据的字节数组

            //字节数组 转成字符串
            String string = new String(bytes, "utf-8");

            //xml转成MAP ---》业务处理。
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(string);
            System.out.println(stringStringMap);

            //判断from的值，如果是 1 就是普通订单 发送消息到普通订单的队列  如果是 2 就是秒杀订单 ：发送到秒杀订单的队列
            String attach = stringStringMap.get("attach");//json格式的字符串 里面有{out_trade_no:12321,totaol_fee:1,from:1}

            Map<String, String> attachmap = JSON.parseObject(attach, Map.class);

            switch (attachmap.get("from")) {
                case "1":
                    System.out.println("普通订单发送");
                    //  发送消到普通队列中
                    //发送消息
                    rabbitTemplate.convertAndSend(
                            environment.getProperty("mq.pay.exchange.order"),
                            environment.getProperty("mq.pay.routing.key"),
                            JSON.toJSONString(stringStringMap)
                    );
                    break;
                case "2":
                    // 发送到秒杀队列中
                    rabbitTemplate.convertAndSend(
                            environment.getProperty("mq.pay.exchange.seckillorder"),
                            environment.getProperty("mq.pay.routing.seckillkey"),
                            JSON.toJSONString(stringStringMap)
                    );
                    System.out.println("秒杀订单发送");
                    break;
                default:
                    //错误的信息
                    System.out.println("错误的信息");
                    break;
            }

            //按照微信的规定返回响应结果给微信支付系统
            Map<String, String> resultmap = new HashMap<>();
            resultmap.put("return_code", "SUCCESS");
            resultmap.put("return_msg", "OK");
            resultxml = WXPayUtil.mapToXml(resultmap);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return resultxml;
    }
}
