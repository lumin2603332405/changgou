package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.SystemConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.seckill.consumer
 * @version 1.0
 * @date 2020/1/17
 */
@Component

@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderPayMessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    @RabbitHandler
    public void consumeMessage(@Payload String message) {
        System.out.println(message);
        //将消息转换成Map对象  包含了所有的通知的数据：包括attach里面有：out_trade_no total_fee from  username
        Map<String, String> resultMap = JSON.parseObject(message, Map.class);

        String attach = resultMap.get("attach");//{}
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);//{}
        String username = attachMap.get("username");
        if (resultMap != null) {
            //通信成功
            if (resultMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
                //支付成功
                if (resultMap.get("result_code").equalsIgnoreCase("SUCCESS")) {
                    //1.根据订单号获取redis中预订单
                    SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).get(username);
                    //2.修改状态
                    seckillOrder.setStatus("1");//已经支付
                    String time_end = resultMap.get("time_end");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date parse = null;
                    try {
                        parse = simpleDateFormat.parse(time_end);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    seckillOrder.setPayTime(parse);
                    seckillOrder.setTransactionId(resultMap.get("transaction_id"));
                    //3.更新到数据库中
                    seckillOrderMapper.insertSelective(seckillOrder);
                    //4.清除掉防止重复排队标记  清除排队抢单的状态的信息   删除预订单
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(username);

                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(username);

                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(username);

                } else {
                    //支付失败
                    //1.恢复库存  redis中恢复库存
                    SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(username);

                    SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
                    if (seckillGoods == null) {
                        seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
                    }
                    //先上锁
                    RLock lock = redissonClient.getLock("myLock");
                    try {
                        lock.lock(10, TimeUnit.SECONDS);
                        seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).put(seckillStatus.getGoodsId(),seckillGoods);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        //释放锁
                        lock.unlock();
                    }
                    //2.清除掉防止重复排队标记  清除排队抢单的状态的信息   删除预订单
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(username);

                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(username);

                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(username);
                }
            }
        }


    }
}
