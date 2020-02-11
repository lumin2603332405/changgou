package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.service
 * @version 1.0
 * @date 2020/1/11
 */
public interface CartService {
    /**
     *   添加购物车
     * @param username  指定要添加的购物车的用户
     * @param id  要买的商品的SKU的ID
     * @param num  要购买的数量
     */
    void add(String username, Long id, Integer num);

    List<OrderItem> list(String username);

}
