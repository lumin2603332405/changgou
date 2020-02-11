package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.listener
 * @version 1.0
 * @date 2020/1/14
 */
@Component
@RabbitListener(queues = "queue.order")
public class PayOrderUpdateListener {

    @Autowired
    private OrderMapper orderMapper;


    //监听队列的方法
    @RabbitHandler
    public void handlerMsg(String msg) {
        //1.获取字符串转成map
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        String out_trade_no = map.get("out_trade_no");
        if (map != null && map.get("return_code").equalsIgnoreCase("SUCCESS")) {
            //支付成功
            if (map.get("result_code").equalsIgnoreCase("SUCCESS")) {
                //2.获取订单的ID  获取交易流水 获取支付时间


                Order order = orderMapper.selectByPrimaryKey(out_trade_no);
                //3.更新订单
                order.setPayStatus("1");//已经支付
                String time_end = map.get("time_end");//支付时间

                //jota-time.jar
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date parse = simpleDateFormat.parse(time_end);
                    order.setPayTime(parse);//设置支付时间
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String transaction_id = map.get("transaction_id");
                order.setTransactionId(transaction_id);
                orderMapper.updateByPrimaryKeySelective(order);
            } else {
                //  支付失败----> 删除订单 ---》恢复库存---》积分恢复
                // 调用关闭订单的API 关闭支付订单
                Order order = orderMapper.selectByPrimaryKey(out_trade_no);
                order.setIsDelete("1");//已经删除 逻辑删除
                order.setPayStatus("2");//支付失败
                orderMapper.updateByPrimaryKeySelective(order);//更新
            }
        }
    }


}
