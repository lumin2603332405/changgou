package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.goods.feign
 * @version 1.0
 * @date 2020/1/3
 */

@FeignClient(name="goods")
public interface SkuFeign {

    /**
     *  根据状态获取到sku的列表
     * @param status
     * @return
     */
    @GetMapping("/sku/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);


    /**
     * 根据商品的ID 获取商品的数据
     * @param id
     * @return
     */
    @GetMapping("/sku/{id}")
    public Result<Sku> findById(@PathVariable("id") Long id);

    /**
     *  给指定的商品的ID 扣库存
     * @param id  要扣库存的商品的ID skuid
     * @param num  要扣的数量
     * @return
     */
    @GetMapping("/sku/decCount")
    public Result decCount(@RequestParam(name="id") Long id, @RequestParam(name="num") Integer num);


}
