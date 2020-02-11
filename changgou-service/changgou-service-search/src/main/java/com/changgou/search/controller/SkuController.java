package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.controller
 * @version 1.0
 * @date 2020/1/3
 */
@RestController
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @RequestMapping("/importES")
    public Result importES() {

        skuService.importES();
        return new Result(true, StatusCode.OK,"导入成功");
    }

    /**
     * 接收页面传递的参数  执行查询 返回结果 map
     * @param searchMap  参数条件封装的对象
     * @return map ： 结果集，品牌列表，分类列表，规格列表，分页信息
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap){
       return skuService.search(searchMap);
    }

}
