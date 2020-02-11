package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.goods.feign
 * @version 1.0
 * @date 2020/1/11
 */
@FeignClient(name="goods")
public interface SpuFeign {
    @GetMapping("/spu/{id}")
    public Result<Spu> findById(@PathVariable(name="id") Long id);
}
