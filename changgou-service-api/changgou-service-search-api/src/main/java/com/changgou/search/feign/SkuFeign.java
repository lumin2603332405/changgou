package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.feign
 * @version 1.0
 * @date 2020/1/6
 */
@FeignClient(name="search")
public interface SkuFeign {

    @GetMapping("/search")
    public Map search(@RequestParam(required = false) Map searchMap);
}
