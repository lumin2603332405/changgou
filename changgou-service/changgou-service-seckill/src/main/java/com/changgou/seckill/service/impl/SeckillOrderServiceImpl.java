package com.changgou.seckill.service.impl;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.thread.MultiThreadingCreateOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import entity.SystemConstants;
import org.apache.commons.lang.RandomStringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/****
 * @Author:admin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;


    /**
     * SeckillOrder条件+分页查询
     *
     * @param seckillOrder 查询条件
     * @param page         页码
     * @param size         页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     *
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder) {
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     *
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder) {
        Example example = new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (seckillOrder != null) {
            // 主键
            if (!StringUtils.isEmpty(seckillOrder.getId())) {
                criteria.andEqualTo("id", seckillOrder.getId());
            }
            // 秒杀商品ID
            if (!StringUtils.isEmpty(seckillOrder.getSeckillId())) {
                criteria.andEqualTo("seckillId", seckillOrder.getSeckillId());
            }
            // 支付金额
            if (!StringUtils.isEmpty(seckillOrder.getMoney())) {
                criteria.andEqualTo("money", seckillOrder.getMoney());
            }
            // 用户
            if (!StringUtils.isEmpty(seckillOrder.getUserId())) {
                criteria.andEqualTo("userId", seckillOrder.getUserId());
            }
            // 创建时间
            if (!StringUtils.isEmpty(seckillOrder.getCreateTime())) {
                criteria.andEqualTo("createTime", seckillOrder.getCreateTime());
            }
            // 支付时间
            if (!StringUtils.isEmpty(seckillOrder.getPayTime())) {
                criteria.andEqualTo("payTime", seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if (!StringUtils.isEmpty(seckillOrder.getStatus())) {
                criteria.andEqualTo("status", seckillOrder.getStatus());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(seckillOrder.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if (!StringUtils.isEmpty(seckillOrder.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", seckillOrder.getReceiverMobile());
            }
            // 收货人
            if (!StringUtils.isEmpty(seckillOrder.getReceiver())) {
                criteria.andEqualTo("receiver", seckillOrder.getReceiver());
            }
            // 交易流水
            if (!StringUtils.isEmpty(seckillOrder.getTransactionId())) {
                criteria.andEqualTo("transactionId", seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean add(Long id, String time, String username) {

        username = UUID.randomUUID().toString();

        //防止重复排队  给当前的用户+1 再判断 是否大于1 如果是，那么说明重复排了。

        Long increment = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).increment(username, 1);
        if(increment>1){
            throw new RuntimeException("重复排队了");
        }


        //先判断再减库存

        //先上锁
        RLock lock = redissonClient.getLock("myLock");

        try {
            lock.lock(10, TimeUnit.SECONDS);
            decrCount(id,time);
        } catch (Exception e) {
            e.printStackTrace();
            //不要走了直接返回
            return false;
        }finally {
            //释放锁
            lock.unlock();
        }







        //将用户状态的信息压入redis队列
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(),1, id,time);

       // redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);
        redisTemplate.boundListOps(SystemConstants.SEC_KILL_USER_QUEUE_KEY).leftPush(seckillStatus);

        //存储用户的排队抢单的状态的信息

       // redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);


        //调用多线程实现下单

        //返回true ：表示下单成功？只能表示：排队中成功

        multiThreadingCreateOrder.createOrder();
       /* */

        return true;
    }

    @Override
    public SeckillStatus getUserSeckillStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(username);
    }

    private void decrCount(Long id, String time){
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);
        //2.判断是否有库存 或者是否商品存在，如果商品部存在 或者库存为0 抛出异常（）
        if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("售罄了");
        }
        //3.减库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //4.保存到redis中
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).put(id, seckillGoods);
    }
}
