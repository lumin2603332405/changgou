package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.service.impl
 * @version 1.0
 * @date 2020/1/11
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(String username, Long id, Integer num) {


        if(num<=0){
            //删除某一个用户的购物车里面的指定的商品
            redisTemplate.boundHashOps("Cart_"+username).delete(id);
            System.out.println("删除成功");
            return ;
        }

        //1.根据ID 调用goods的feign获取商品的数据  根据SPU的ID 获取SPU的数据
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        Spu spu = spuFeign.findById(sku.getSpuId()).getData();


        //2.将数据存储到redis的 某一个登录的用户的 购物车中 key: value
                // string  hash list set zset    String :key   value: list<POJO>-->json
               //  bigkey   field  value
                // szitheima  id1     pojo1:orderItem
                // zitheima   id2    pojo2
                // lisi  id1     pojo1
                // lisi   id2    pojo2

        //3. 转换成ORDERitem
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());//todo
        orderItem.setCategoryId2(spu.getCategory2Id());//todo
        orderItem.setCategoryId3(spu.getCategory3Id());//todo
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);//设置购买的数量
        orderItem.setMoney(sku.getPrice()*num);
        orderItem.setPayMoney(sku.getPrice()*num);
        orderItem.setImage(sku.getImage());
        orderItem.setIsReturn("0");

        redisTemplate.boundHashOps("Cart_"+username).put(id ,orderItem);

    }

    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> values = redisTemplate.boundHashOps("Cart_" + username).values();
        return values;
    }
}
