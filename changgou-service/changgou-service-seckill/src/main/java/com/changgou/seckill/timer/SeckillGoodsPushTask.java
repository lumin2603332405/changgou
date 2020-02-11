package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.seckill.timer
 * @version 1.0
 * @date 2020/1/15
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //用于反复被执行
    //corn = 用于指定何时执行的表达式  从0秒开始 每隔5秒钟执行一次。
    @Scheduled(cron = "0/10 * * * * ? ")
    public void loadGoodsPushRedis() {
        List<Date> dateMenus = DateUtil.getDateMenus();

        for (Date dateMenu : dateMenus) {
            String extName = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
            //5个时间段
            //1.查询符合条件的数据库的秒杀的商品的数据
            Example exmaple = new Example(SeckillGoods.class);
            Example.Criteria criteria = exmaple.createCriteria();

            criteria.andEqualTo("status", "1");//status=1
            criteria.andGreaterThan("stockCount", 0);//stockCount>0
            criteria.andEqualTo("startTime", dateMenu);

            criteria.andLessThan("endTime", DateUtil.addDateHour(dateMenu, 2));

            // id  not in (redis中已有的id)
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();
            if(keys!=null&& keys.size()>0 ) {
                criteria.andNotIn("id", keys);
            }

            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(exmaple);

            //2.将数据压入到redis中
            for (SeckillGoods seckillGood : seckillGoods) {

                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + extName).put(seckillGood.getId(),seckillGood);
                //设置有效期
                redisTemplate.expireAt(SystemConstants.SEC_KILL_GOODS_PREFIX + extName,DateUtil.addDateHour(dateMenu, 2));

            }

        }

    }

    public static void main(String[] args) {

        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            System.out.println(dateMenu);
        }
    }
}
