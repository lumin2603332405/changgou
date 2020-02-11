package com.changgou.search.service;

import java.util.Map; /***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.service
 * @version 1.0
 * @date 2020/1/3
 */
public interface SkuService {
    /**
     * //1.调用商品微服务的feign 查询到符合条件的所有的SKU的数据
     //2.调用spring data elasticsearch的API 数据存储到ES服务器中
     */
    void importES();

    Map search(Map<String,String> searchMap);
}
