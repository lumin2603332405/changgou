package com.changgou.pay.service;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.pay.service
 * @version 1.0
 * @date 2020/1/14
 */
public interface WeixinPayService {

    Map createNative(Map<String,String> parameters);

    Map<String,String> queryStatus(String out_trade_no);

}
